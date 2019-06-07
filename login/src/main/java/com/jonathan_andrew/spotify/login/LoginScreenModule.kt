package com.jonathan_andrew.spotify.login

import com.jonathan_andrew.spotify.domain.entities.auth.LoginCallback
import com.jonathan_andrew.spotify.domain.entities.auth.LoginRequestCode
import com.jonathan_andrew.spotify.domain.entities.auth.LoginResultCode
import com.jonathan_andrew.spotify.domain.use_cases.UseCase
import com.jonathan_andrew.spotify.domain.use_cases.auth.LoginAction
import com.jonathan_andrew.spotify.domain.use_cases.auth.LoginResult
import com.jonathan_andrew.spotify.login.domain.LoginUseCase
import com.jonathan_andrew.spotify.login.ui.LoginActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject

@Module
abstract class LoginScreenModule {
    @Module
    companion object {
        @Provides
        @JvmStatic
        fun loginCallbacksSubject(): PublishSubject<LoginCallback> = PublishSubject.create()

        @Provides
        @JvmStatic
        fun loginResultCodesSubject(): PublishSubject<LoginResultCode> = PublishSubject.create()

        @Provides
        @JvmStatic
        fun requestCode(): LoginRequestCode = LoginActivity.LOGIN_REQUEST_CODE
    }

    @Binds
    abstract fun loginCallbacksObservable(
            impl: PublishSubject<LoginCallback>
    ): Observable<LoginCallback>

    @Binds
    abstract fun loginResultCodesObservable(
            impl: PublishSubject<LoginResultCode>
    ): Observable<LoginResultCode>

    @Binds
    abstract fun loginCallbacksConsumer(
            impl: PublishSubject<LoginCallback>
    ): Observer<LoginCallback>

    @Binds
    abstract fun loginResultCodesConsumer(
            impl: PublishSubject<LoginResultCode>
    ): Observer<LoginResultCode>

    @Binds
    abstract fun loginUseCase(impl: LoginUseCase): UseCase<LoginAction, LoginResult>
}