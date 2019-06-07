package com.jonathan_andrew.spotify.domain.use_cases.auth

import android.app.Activity
import com.jonathan_andrew.spotify.domain.entities.Credentials
import io.reactivex.Observable

interface LoginManager {
    fun login(): Observable<Credentials>
}