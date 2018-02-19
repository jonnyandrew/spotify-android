package com.jonathan_andrew.spotify.domain.use_cases.artists

import com.jonathan_andrew.spotify.domain.use_cases.UseCase
import io.reactivex.Observable
import io.reactivex.ObservableSource

class GetArtistUseCase(val artistsRepository: ArtistsRepository) : UseCase<GetArtistAction, GetArtistResult> {
    override fun apply(upstream: Observable<GetArtistAction>): ObservableSource<GetArtistResult> {
        return upstream
                .switchMap {
                    artistsRepository.get(it.id)
                            .map { GetArtistResult.Success(it) }
                            .cast(GetArtistResult::class.java)
                            .onErrorReturn {
                                GetArtistResult.Failure(it)
                            }
                            .startWith(GetArtistResult.InProgress())
                }
    }
}