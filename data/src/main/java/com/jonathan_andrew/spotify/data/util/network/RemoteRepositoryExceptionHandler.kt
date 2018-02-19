package com.jonathan_andrew.spotify.data.util.network

import com.jonathan_andrew.spotify.domain.entities.exceptions.RepositoryException
import com.jonathan_andrew.spotify.domain.entities.exceptions.UnauthorizedException
import io.reactivex.Single
import io.reactivex.SingleSource

class RemoteRepositoryExceptionHandler<T> : io.reactivex.functions.Function<Throwable, SingleSource<T>> {
    override fun apply(t: Throwable): SingleSource<T> {
        return Single.error(
                when (t) {
                    is UnauthorizedException -> t
                    else -> RepositoryException(t)
                }
        )
    }
}
