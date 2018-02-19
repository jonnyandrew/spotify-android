package com.jonathan_andrew.spotify.ui.artist

sealed class ArtistUiEvent {
    class Load(val artistId: String) : ArtistUiEvent()
}