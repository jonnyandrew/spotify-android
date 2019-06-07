package com.jonathan_andrew.spotify.search

import com.jonathan_andrew.spotify.search.ui.SearchIntentFactoryImpl
import com.jonathan_andrew.spotify.ui.navigation.SearchIntentFactory
import dagger.Binds
import dagger.Module

@Module
abstract class SearchScreenModule {
    @Binds
    abstract fun searchIntentFactory(
            impl: SearchIntentFactoryImpl
    ): SearchIntentFactory
}