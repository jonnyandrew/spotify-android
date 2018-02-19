package com.jonathan_andrew.spotify.ui.login

sealed class LoginUiEvent {
    class ClickLogin : LoginUiEvent()
}