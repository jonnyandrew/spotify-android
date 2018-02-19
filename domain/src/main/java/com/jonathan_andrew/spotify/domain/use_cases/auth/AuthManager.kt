package com.jonathan_andrew.spotify.domain.use_cases.auth

import com.jonathan_andrew.spotify.domain.entities.Credentials
import io.reactivex.Observable
import io.reactivex.Single

interface AuthManager {
    fun getCredentials(): Observable<Credentials>
    fun setCredentials(credentials: Credentials): Single<Credentials>
}