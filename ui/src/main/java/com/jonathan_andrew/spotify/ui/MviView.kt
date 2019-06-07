package com.jonathan_andrew.spotify.ui

import io.reactivex.Observable


interface MviView<E, in M> {
    val events: Observable<E>
    fun setUiModel(model: M)
}