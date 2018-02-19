package com.jonathan_andrew.spotify.domain.use_cases.artists

import com.jonathan_andrew.spotify.domain.use_cases.Action

data class GetArtistAction(val id: String) : Action
