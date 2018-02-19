package com.jonathan_andrew.spotify.data.util.network

import com.jonathan_andrew.spotify.domain.entities.exceptions.UnauthorizedException
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

fun Response.detectProblemsAndThrow() {
    if (this.code() == 401) throw UnauthorizedException()
    if (!this.isSuccessful) throw IOException("Unexpected code " + this.code())
}

fun Response.getNonNullBodyOrThrow(): ResponseBody {
    val body = this.body()
    if (body == null) {
        throw IOException("Empty response")
    }
    return body
}
