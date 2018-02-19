package com.jonathan_andrew.spotify.data.artists

import com.beust.klaxon.Klaxon
import com.jonathan_andrew.spotify.data.util.network.detectProblemsAndThrow
import com.jonathan_andrew.spotify.data.util.network.getNonNullBodyOrThrow
import io.reactivex.Single
import io.reactivex.Single.defer
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class ArtistApi(val httpClient: OkHttpClient) {
    fun get(id: String): Single<ArtistApiResponse> {
        return defer {
            val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.spotify.com")
                    .addPathSegments("v1/artists")
                    .addPathSegment(id)
                    .build()
            val request = Request.Builder()
                    .url(url)
                    .build()

            val response = httpClient.newCall(request).execute()

            response.detectProblemsAndThrow()

            val responseJson = response.getNonNullBodyOrThrow().string()

            val result = Klaxon().parse<ArtistApiResponse>(responseJson)
            Single.just(result)
        }
    }
}