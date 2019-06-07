package com.jonathan_andrew.spotify

import android.app.Application
import android.support.text.emoji.EmojiCompat
import android.support.text.emoji.bundled.BundledEmojiCompatConfig
import com.jonathan_andrew.spotify.data.util.database.Database
import com.jonathan_andrew.spotify.di.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject


class App : Application(), HasAndroidInjector {
    companion object Singletons {
        lateinit var instance: App
    }

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var httpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        instance = this
        DaggerAppComponent.builder()
                .applicationContext(applicationContext)
                .build().inject(this)
        initLeakDetector()
        initLogging()
        initEmojiCompat()
        initDatabase()

    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    private fun initEmojiCompat() {
        val config = BundledEmojiCompatConfig(this)
        EmojiCompat.init(config)
    }

    private fun initLogging() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initDatabase() {
        Database.init(this)
    }

    private fun initLeakDetector() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }
}