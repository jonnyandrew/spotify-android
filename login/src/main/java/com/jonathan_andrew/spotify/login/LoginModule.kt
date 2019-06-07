package com.jonathan_andrew.spotify.login

import com.jonathan_andrew.spotify.domain.use_cases.auth.CredentialsRepository
import com.jonathan_andrew.spotify.login.data.LocalCredentialsRepository
import dagger.Binds
import dagger.Module

@Module
abstract class LoginModule {
    @Binds
    abstract fun credentialsRepository(impl: LocalCredentialsRepository): CredentialsRepository
}