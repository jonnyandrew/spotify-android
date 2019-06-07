package com.jonathan_andrew.spotify.data.auth

import com.jonathan_andrew.spotify.domain.entities.auth.LoginCallback
import com.jonathan_andrew.spotify.domain.entities.auth.LoginResultCode
import com.jonathan_andrew.spotify.domain.use_cases.auth.LoginManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

@Module
abstract class AuthDataModule {
    @Module
    companion object {
        @Provides
        @JvmStatic
        fun loginCallbacksSubject(): PublishSubject<LoginCallback> = PublishSubject.create()

        @Provides
        @JvmStatic
        fun loginResultCodesSubject(): PublishSubject<LoginResultCode> = PublishSubject.create()
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
    abstract fun loginManager(impl: RemoteLoginManager): LoginManager
}