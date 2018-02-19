package com.jonathan_andrew.spotify.data.search

import com.jonathan_andrew.spotify.data.artists.ArtistApiResponse
import com.jonathan_andrew.spotify.data.tracks.TrackApiResponse

data class SearchApiResponse(
        val tracks: SearchApiTracksResponse = SearchApiTracksResponse(),
        val artists: SearchApiArtistsResponse = SearchApiArtistsResponse()
)

data class SearchApiTracksResponse(
        val items: List<TrackApiResponse> = listOf()
)

data class SearchApiArtistsResponse(
        val items: List<ArtistApiResponse> = listOf()
)

