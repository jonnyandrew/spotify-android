package com.jonathan_andrew.spotify

import android.app.Application
import android.support.text.emoji.EmojiCompat
import android.support.text.emoji.bundled.BundledEmojiCompatConfig
import com.jonathan_andrew.spotify.data.auth.LocalAuthManager
import com.jonathan_andrew.spotify.data.util.database.Database
import com.jonathan_andrew.spotify.data.util.network.ApiHttpClientFactory
import com.jonathan_andrew.spotify.domain.use_cases.auth.AuthManager
import com.squareup.leakcanary.LeakCanary
import okhttp3.OkHttpClient
import timber.log.Timber

class App : Application() {

    companion object Singletons {
        lateinit var instance: App
    }

    val authManager: AuthManager by lazy {
        LocalAuthManager(this)
    }

    val httpClient: OkHttpClient by lazy {
        ApiHttpClientFactory.createHttpClient(authManager)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initLeakDetector()
        initLogging()
        initEmojiCompat()
        initDatabase()
    }

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