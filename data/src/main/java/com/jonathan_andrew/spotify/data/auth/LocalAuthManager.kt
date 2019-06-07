package com.jonathan_andrew.spotify.data.auth

import android.content.Context
import com.jonathan_andrew.spotify.domain.entities.Credentials
import com.jonathan_andrew.spotify.domain.use_cases.auth.AuthManager
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Stores and retrieves the API access token in the shared preferences.
 */
class LocalAuthManager @Inject constructor(
        context: Context
) : AuthManager {

    private val prefs = context.getSharedPreferences(PREFS_CREDENTIAL, Context.MODE_PRIVATE)

    private companion object {
        val PREFS_CREDENTIAL = "prefs_credentials"
        val PREF_CREDENTIAL_TOKEN = "pref_credential_token"
    }

    override fun getCredentials(): Observable<Credentials> {
        return Observable.defer {
            if (prefs.contains(PREF_CREDENTIAL_TOKEN)) {
                val token = prefs.getString(PREF_CREDENTIAL_TOKEN, null)
                Observable.just(Credentials.Authorized(token))
            } else {
                Observable.just(Credentials.Unauthorized())
            }
        }
    }

    override fun setCredentials(credentials: Credentials): Single<Credentials> {
        return Single.defer {
            prefs.edit().apply {
                when (credentials) {
                    is Credentials.Unauthorized ->
                        remove(PREF_CREDENTIAL_TOKEN)
                    is Credentials.Authorized ->
                        putString(PREF_CREDENTIAL_TOKEN, credentials.token)
                }
            }.apply()
            Single.just(credentials)
        }
    }
}