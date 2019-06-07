package com.jonathan_andrew.spotify.data.util.network

import com.jonathan_andrew.spotify.data.BuildConfig
import com.jonathan_andrew.spotify.domain.use_cases.auth.CredentialsRepository
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

@Module
abstract class NetworkModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun okHttpClient(credentialsRepository: CredentialsRepository): OkHttpClient {
            return OkHttpClient.Builder()
                    .addInterceptor(AuthenticationInterceptor(credentialsRepository))
                    .addInterceptor(StandardHeadersInterceptor())
                    .apply {
                        if (BuildConfig.DEBUG) {
                            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                        }
                    }
                    .build()
        }
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


