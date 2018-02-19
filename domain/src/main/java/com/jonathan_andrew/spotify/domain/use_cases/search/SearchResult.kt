package com.jonathan_andrew.spotify.domain.use_cases.search

import com.jonathan_andrew.spotify.domain.entities.Artist
import com.jonathan_andrew.spotify.domain.entities.Track
import com.jonathan_andrew.spotify.domain.use_cases.Result

sealed class SearchResult : Result {
    class Success(val tracks: List<Track>, val artists: List<Artist>) : SearchResult()
    class InProgress : SearchResult()
    class Failure(val throwable: Throwable) : SearchResult()
}
