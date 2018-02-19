package com.jonathan_andrew.spotify.domain.entities

data class Track(
        val id: String,
        val name: String,
        val artists: List<Artist>
)
