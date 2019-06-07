package com.jonathan_andrew.spotify.login.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.RxView
import com.jonathan_andrew.spotify.domain.entities.auth.LoginCallback
import com.jonathan_andrew.spotify.domain.entities.auth.LoginResultCode
import com.jonathan_andrew.spotify.login.R
import com.jonathan_andrew.spotify.ui.MviView
import com.jonathan_andrew.spotify.ui.navigation.SearchIntentFactory
import dagger.Binds
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), MviView<LoginUiEvent, LoginUiModel> {
    companion object {
        const val LOGIN_REQUEST_CODE = 1

        fun startActivity(context: Context) {
            context.startActivity(
                    Intent(context, LoginActivity::class.java)
                            .addFlags(FLAG_ACTIVITY_SINGLE_TOP)
            )
        }
    }

    @Inject
    internal lateinit var presenter: LoginMviPresenter

    @Inject
    internal lateinit var loginCallbacks: Observer<LoginCallback>

    @Inject
    internal lateinit var loginResultCodes: Observer<LoginResultCode>

    @Inject
    lateinit var searchIntentFactory: SearchIntentFactory

    private var finished: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_login)
        presenter.begin(this)
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
        startActivity(searchIntentFactory.create())
    }

    @dagger.Module
    abstract class Module {
        @Binds
        abstract fun activity(loginActivity: LoginActivity): Activity
    }
}


