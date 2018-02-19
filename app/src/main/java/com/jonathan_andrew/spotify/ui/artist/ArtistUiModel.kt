package com.jonathan_andrew.spotify.ui.artist

data class ArtistUiModel(
        val loading: Boolean,
        val status: Int,
        val error: Boolean,
        val loggedIn: Boolean,
        val name: String,
        val imageUrl: String?,
        val followers: Int,
        val genre: String,
        val popularity: Int
)
