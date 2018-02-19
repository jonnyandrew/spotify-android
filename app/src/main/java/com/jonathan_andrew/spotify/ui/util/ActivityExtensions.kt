package com.jonathan_andrew.spotify.ui.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

internal fun Activity.hideKeyboard() {
    // Check if no view has focus:
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
