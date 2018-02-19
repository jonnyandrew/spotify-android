package com.jonathan_andrew.spotify.data.search

import com.beust.klaxon.Klaxon
import com.jonathan_andrew.spotify.data.util.network.detectProblemsAndThrow
import com.jonathan_andrew.spotify.data.util.network.getNonNullBodyOrThrow
import io.reactivex.Single
import io.reactivex.Single.defer
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request


class SearchApi(val httpClient: OkHttpClient) {

    fun searchTracks(query: String): Single<SearchApiResponse> {
        return search(query, "track")
    }

    fun searchArtists(query: String): Single<SearchApiResponse> {
        return search(query, "artist")
    }

    private fun search(query: String, type: String): Single<SearchApiResponse> {
        return defer {
            val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.spotify.com")
                    .addPathSegments("v1/search")
                    .addQueryParameter("q", query)
                    .addQueryParameter("type", type)
                    .build()
            val request = Request.Builder()
                    .url(url)
                    .build()

            val response = httpClient.newCall(request).execute()

            response.detectProblemsAndThrow()

            val responseJson = response.getNonNullBodyOrThrow().string()

            val result = Klaxon().parse<SearchApiResponse>(responseJson)
            Single.just(result)
        }
    }
}