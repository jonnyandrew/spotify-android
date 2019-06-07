package com.jonathan_andrew.spotify.data.auth

import com.jonathan_andrew.spotify.domain.use_cases.auth.AuthManager
import dagger.Binds
import dagger.Module

@Module
abstract class AuthModule {
    @Binds
    abstract fun authManager(impl: LocalAuthManager): AuthManager
}