package com.jonathan_andrew.spotify.ui.search

data class SearchUiModel(
        val loading: Boolean,
        val status: Int,
        val error: Boolean,
        val tracks: List<TrackSearchResultUiModel>,
        val artists: List<ArtistSearchResultUiModel>,
        val loggedOut: Boolean
)

data class TrackSearchResultUiModel(
        val title: String,
        val artists: String
)

data class ArtistSearchResultUiModel(
        val id: String,
        val name: String,
        val imageUrl: String?
)
