package com.jonathan_andrew.spotify.ui

import io.reactivex.Observable


internal interface MviView<E, in M> {
    val events: Observable<E>
    fun setUiModel(model: M)
}