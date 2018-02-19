package com.jonathan_andrew.spotify.domain.entities

sealed class Credentials {
    class Unauthorized : Credentials()
    class Authorized(val token: String) : Credentials()
}
