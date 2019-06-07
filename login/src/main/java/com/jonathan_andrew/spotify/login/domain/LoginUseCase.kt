package com.jonathan_andrew.spotify.login.domain

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.jonathan_andrew.spotify.domain.entities.Credentials
import com.jonathan_andrew.spotify.domain.entities.auth.LoginRequestCode
import com.jonathan_andrew.spotify.domain.use_cases.UseCase
import com.jonathan_andrew.spotify.domain.use_cases.auth.CredentialsRepository
import com.jonathan_andrew.spotify.domain.use_cases.auth.LoginAction
import com.jonathan_andrew.spotify.domain.use_cases.auth.LoginResult
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.rxkotlin.merge
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoginUseCase @Inject constructor(
        private val context: Activity,
        private val credentialsRepository: CredentialsRepository,
        private val loginCallbackUris: Observable<String>,
        private val loginResultCodes: Observable<Int>,
        private val activityLoginRequestCode: LoginRequestCode
) : UseCase<LoginAction, LoginResult> {

    private lateinit var randomState: String

    override fun apply(upstream: Observable<LoginAction>): ObservableSource<LoginResult> {
        return upstream.switchMap {
            login()
                    .map { credentials ->
                        when (credentials) {
                            is Credentials.Authorized -> LoginResult.Authenticated()
                            is Credentials.Unauthorized -> LoginResult.Unauthenticated()
                        }
                    }.startWith(LoginResult.InProgress())
        }
    }

    private fun login(): Observable<Credentials> {
        return credentialsRepository.get()
                .flatMap { credentials ->
                    when (credentials) {
                        is Credentials.Unauthorized ->
                            loginWithOauth()
                        is Credentials.Authorized ->
                            Observable.just(credentials)

                    }
                }
    }

    private fun loginWithOauth(): Observable<Credentials> {
        startOauthFlow()

        // Detect a success if the login callback is good
        // Note that the oauth callback may still have received and error
        val successResults = loginCallbackUris
                .map { URI.create(it) }
                .filter { uri -> uri.host == "login-callback" && uri.scheme == "jonathan-andrew-spotify" }
                .firstElement()
                .flatMapSingle { uri ->
                    val token = parseToken(uri)
                    val credentials = if (token != null) {
                        Credentials.Authorized(token)
                    } else {
                        Credentials.Unauthorized()
                    }
                    credentialsRepository.set(credentials)
                }
                .toObservable()

        // Detect a failure if the oauth window is closed and there hasn't been a success
        // login callback in the last second
        val failureResults = loginResultCodes
                .filter { code -> code == Activity.RESULT_CANCELED }
                .flatMapSingle { credentialsRepository.set(Credentials.Unauthorized()) }
                .delay(500, TimeUnit.MILLISECONDS)

        // We take the result to be whichever comes first, success or failure
        return listOf(successResults, failureResults).merge().firstElement().toObservable()
    }

    private fun startOauthFlow() {
        // TODO: Remove hardcoded client id etc.
        randomState = UUID.randomUUID().toString()
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://accounts.spotify.com/authorize" +
                        "?client_id=84ea753e599142b8bace9b63d153227b" +
                        "&response_type=token" +
                        "&redirect_uri=jonathan-andrew-spotify://login-callback" +
                        "&scope=user-read-private" +
                        "&show_dialog=true" +
                        "&state=" + randomState
        ))
        context.startActivityForResult(browserIntent, activityLoginRequestCode)
    }

    private fun parseToken(uri: URI): String? {
        if (uri.fragment == null) {
            return null
        }

        val keyValues = uri.fragment.split("&").map {
            val array = it.split("=")
            array[0] to array[1]
        }.toMap()

        // Check the state is the same as the one we passed to the server
        if (keyValues.get("state") != randomState) {
            return null
        }

        return keyValues.get("access_token")
    }
}
