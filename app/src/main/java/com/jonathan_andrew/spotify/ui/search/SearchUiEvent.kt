package com.jonathan_andrew.spotify.ui.search

sealed class SearchUiEvent {
    class ChangeSearch(val search: String) : SearchUiEvent()
}