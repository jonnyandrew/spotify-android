package com.jonathan_andrew.spotify.data.tracks

data class TrackApiResponse(
        val id: String,
        val name: String,
        val artists: List<TrackArtistApiResponse>
)

data class TrackArtistApiResponse(
        val id: String,
        val name: String
)

