package com.jonathan_andrew.spotify.login.ui

sealed class LoginUiEvent {
    class ClickLogin : LoginUiEvent()
}