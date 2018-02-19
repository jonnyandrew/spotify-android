package com.jonathan_andrew.spotify.domain.use_cases.auth

import com.jonathan_andrew.spotify.domain.use_cases.Result

sealed class LoginResult : Result {
    class Authenticated : LoginResult()
    class Unauthenticated : LoginResult()
    class InProgress : LoginResult()
}
