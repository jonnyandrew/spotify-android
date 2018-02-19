package com.jonathan_andrew.spotify.domain.use_cases.artists

import com.jonathan_andrew.spotify.domain.entities.Artist
import com.jonathan_andrew.spotify.domain.use_cases.Result

sealed class GetArtistResult : Result {
    class Success(val artist: Artist) : GetArtistResult()
    class InProgress : GetArtistResult()
    class Failure(val throwable: Throwable) : GetArtistResult()
}
