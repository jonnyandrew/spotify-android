package com.jonathan_andrew.spotify.ui.navigation

import android.content.Intent

interface SearchIntentFactory {
    fun create(): Intent
}