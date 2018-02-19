package com.jonathan_andrew.spotify.domain.use_cases.auth

import com.jonathan_andrew.spotify.domain.entities.Credentials
import com.jonathan_andrew.spotify.domain.use_cases.UseCase
import io.reactivex.Observable
import io.reactivex.ObservableSource

class LoginUseCase(private val loginManager: LoginManager) : UseCase<LoginAction, LoginResult> {
    override fun apply(upstream: Observable<LoginAction>): ObservableSource<LoginResult> {
        return upstream.switchMap {
            loginManager.login()
                    .map { credentials ->
                        when (credentials) {
                            is Credentials.Authorized -> LoginResult.Authenticated()
                            is Credentials.Unauthorized -> LoginResult.Unauthenticated()
                        }
                    }.startWith(LoginResult.InProgress())
        }
    }
}

