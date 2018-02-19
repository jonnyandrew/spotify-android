package com.jonathan_andrew.spotify.domain.use_cases.search

import com.jonathan_andrew.spotify.domain.use_cases.Action

data class SearchAction(val query: String) : Action
