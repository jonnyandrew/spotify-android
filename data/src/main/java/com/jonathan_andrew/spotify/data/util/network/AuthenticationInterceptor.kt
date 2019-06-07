package com.jonathan_andrew.spotify.data.util.network

import com.jonathan_andrew.spotify.domain.entities.Credentials
import com.jonathan_andrew.spotify.domain.use_cases.auth.CredentialsRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor(private val credentialsRepository: CredentialsRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val credentials = credentialsRepository.get().blockingFirst()

        when (credentials) {
            is Credentials.Unauthorized -> {
                return chain.proceed(original)
            }
            is Credentials.Authorized -> {
                val builder = original.newBuilder()
                        .header("Authorization", "Bearer " + credentials.token)
                val authenticatedRequest = builder.build()
                val response = chain.proceed(authenticatedRequest)

                if (response.code() == 401) {
                    credentialsRepository.set(Credentials.Unauthorized()).blockingGet()
                }
                return response
            }
        }
    }
}
