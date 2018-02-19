package com.jonathan_andrew.spotify.data.util.network

import com.jonathan_andrew.spotify.data.BuildConfig
import com.jonathan_andrew.spotify.domain.use_cases.auth.AuthManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

object ApiHttpClientFactory {
    fun createHttpClient(authManager: AuthManager): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(AuthenticationInterceptor(authManager))
                .addInterceptor(StandardHeadersInterceptor())
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    }
                }
                .build()
    }

    /**
     * Ensures all HTTP requests have the correct base headers.
     */
    private class StandardHeadersInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val builder = original.newBuilder()
                    .header("Content-Type", "application/json")

            val request = builder.build()
            return chain.proceed(request)
        }
    }
}


