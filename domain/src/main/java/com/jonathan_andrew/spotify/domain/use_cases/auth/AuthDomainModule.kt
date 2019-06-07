package com.jonathan_andrew.spotify.domain.use_cases.auth

import com.jonathan_andrew.spotify.domain.use_cases.UseCase
import dagger.Binds
import dagger.Module

@Module
abstract class AuthDomainModule {
    @Binds
    abstract fun loginUseCase(impl: LoginUseCase): UseCase<LoginAction, LoginResult>
}