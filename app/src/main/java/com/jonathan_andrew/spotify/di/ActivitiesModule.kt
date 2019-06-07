package com.jonathan_andrew.spotify.di

import com.jonathan_andrew.spotify.domain.di.ActivityScope
import com.jonathan_andrew.spotify.login.LoginScreenModule
import com.jonathan_andrew.spotify.login.ui.LoginActivity
import com.jonathan_andrew.spotify.search.SearchScreenModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = [
        LoginActivity.Module::class,
        LoginScreenModule::class,
        SearchScreenModule::class
    ])
    @ActivityScope
    abstract fun loginActivity(): LoginActivity
}