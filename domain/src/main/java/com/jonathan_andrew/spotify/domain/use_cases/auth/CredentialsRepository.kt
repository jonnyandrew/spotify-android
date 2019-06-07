package com.jonathan_andrew.spotify.domain.use_cases.auth

import com.jonathan_andrew.spotify.domain.entities.Credentials
import io.reactivex.Observable
import io.reactivex.Single

interface CredentialsRepository {
    fun get(): Observable<Credentials>
    fun set(credentials: Credentials): Single<Credentials>
}