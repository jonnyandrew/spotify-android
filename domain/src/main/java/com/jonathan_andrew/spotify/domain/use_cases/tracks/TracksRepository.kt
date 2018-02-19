package com.jonathan_andrew.spotify.domain.use_cases.tracks

import com.jonathan_andrew.spotify.domain.entities.Track
import io.reactivex.Single

interface TracksRepository {
    fun search(query: String): Single<List<Track>>
}