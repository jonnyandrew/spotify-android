package com.jonathan_andrew.spotify.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.RxView
import com.jonathan_andrew.spotify.R
import com.jonathan_andrew.spotify.domain.entities.auth.LoginCallback
import com.jonathan_andrew.spotify.domain.entities.auth.LoginRequestCode
import com.jonathan_andrew.spotify.domain.entities.auth.LoginResultCode
import com.jonathan_andrew.spotify.ui.MviView
import com.jonathan_andrew.spotify.ui.search.SearchActivity
import dagger.Binds
import dagger.Provides
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.rxkotlin.merge
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), MviView<LoginUiEvent, LoginUiModel> {
    companion object {
        private const val LOGIN_REQUEST_CODE = 1

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    @Inject
    internal lateinit var presenter: LoginMviPresenter

    @Inject
    internal lateinit var loginCallbacks: PublishSubject<LoginCallback>

    @Inject
    internal lateinit var loginResultCodes: PublishSubject<LoginResultCode>

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
        SearchActivity.startActivity(this)
    }

    @dagger.Module
    abstract class Module {
        @dagger.Module
        companion object {
            @Provides
            @JvmStatic
            fun requestCode(): LoginRequestCode = LOGIN_REQUEST_CODE
        }

        @Binds
        abstract fun activity(loginActivity: LoginActivity): Activity
    }
}


