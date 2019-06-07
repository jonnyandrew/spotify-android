package com.jonathan_andrew.spotify.di

import com.jonathan_andrew.spotify.data.auth.AuthDataModule
import com.jonathan_andrew.spotify.domain.di.ActivityScope
import com.jonathan_andrew.spotify.domain.use_cases.auth.AuthDomainModule
import com.jonathan_andrew.spotify.ui.login.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = [
        LoginActivity.Module::class,
        AuthDomainModule::class,
        AuthDataModule::class
    ])
    @ActivityScope
    abstract fun loginActivity(): LoginActivity
}