package com.jonathan_andrew.spotify.domain.use_cases.artists

import com.jonathan_andrew.spotify.domain.entities.Artist
import io.reactivex.Observable
import io.reactivex.Single

interface ArtistsRepository {
    fun search(query: String): Single<List<Artist>>
    fun get(id: String): Observable<Artist>
    fun put(artist: Artist): Single<Artist>
}