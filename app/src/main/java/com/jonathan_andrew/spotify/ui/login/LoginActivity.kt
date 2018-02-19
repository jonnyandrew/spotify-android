package com.jonathan_andrew.spotify.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.RxView
import com.jonathan_andrew.spotify.App
import com.jonathan_andrew.spotify.R
import com.jonathan_andrew.spotify.data.auth.RemoteLoginManager
import com.jonathan_andrew.spotify.domain.use_cases.auth.LoginUseCase
import com.jonathan_andrew.spotify.ui.MviView
import com.jonathan_andrew.spotify.ui.search.SearchActivity
import io.reactivex.Observable
import io.reactivex.rxkotlin.merge
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), MviView<LoginUiEvent, LoginUiModel> {
    companion object {
        private val LOGIN_REQUEST_CODE = 1

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    private val loginCallbacks: PublishSubject<String> = PublishSubject.create()
    private val loginResultCodes: PublishSubject<Int> = PublishSubject.create()

    private val presenter by lazy {
        LoginMviPresenter(
                this,
                LoginUseCase(
                        RemoteLoginManager(this,
                                App.instance.authManager,
                                loginCallbacks,
                                loginResultCodes,
                                LOGIN_REQUEST_CODE
                        )
                )
        )
    }

    private var finished: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter.begin()
    }

    override val events: Observable<LoginUiEvent>
            by lazy {
                listOf(loginClicks)
                        .merge()
                        .cast(LoginUiEvent::class.java)
            }

    private val loginClicks by lazy {
        RxView.clicks(login).map { LoginUiEvent.ClickLogin() }
    }

    override fun setUiModel(model: LoginUiModel) {
        login.isEnabled = !model.loading
        statusMessage.text = resources.getString(model.message)

        if (model.loggedIn) {
            navigateToSearchActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.resume()
    }

    override fun onStop() {
        presenter.pause()
        super.onStop()
    }

    override fun onDestroy() {
        presenter.end()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent.data
        if (uri.scheme == "jonathan-andrew-spotify" && uri.host == "login-callback") {
            loginCallbacks.onNext(uri.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_REQUEST_CODE) {
            Activity.RESULT_CANCELED
            loginResultCodes.onNext(resultCode)
        }
    }

    private fun navigateToSearchActivity() {
        if (finished) {
            return
        }
        finished = true
        finish()
        SearchActivity.startActivity(this)
    }

}


