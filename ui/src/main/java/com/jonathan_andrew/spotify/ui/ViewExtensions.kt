package com.jonathan_andrew.spotify.ui

import android.view.View

var View.gone: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if (value) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

var View.invisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }
