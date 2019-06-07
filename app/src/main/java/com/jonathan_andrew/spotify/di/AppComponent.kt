package com.jonathan_andrew.spotify.di

import android.content.Context
import com.jonathan_andrew.spotify.App
import com.jonathan_andrew.spotify.data.auth.AuthModule
import com.jonathan_andrew.spotify.data.util.network.NetworkModule
import com.jonathan_andrew.spotify.domain.di.ApplicationScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule


@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivitiesModule::class,
    NetworkModule::class,
    AuthModule::class
])
@ApplicationScope
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(applicationContext: Context): Builder

        fun build(): AppComponent
    }
}