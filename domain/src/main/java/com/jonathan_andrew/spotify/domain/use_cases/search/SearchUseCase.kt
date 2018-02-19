package com.jonathan_andrew.spotify.domain.use_cases.search

import com.jonathan_andrew.spotify.domain.entities.Artist
import com.jonathan_andrew.spotify.domain.entities.Track
import com.jonathan_andrew.spotify.domain.use_cases.UseCase
import com.jonathan_andrew.spotify.domain.use_cases.artists.ArtistsRepository
import com.jonathan_andrew.spotify.domain.use_cases.tracks.TracksRepository
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

class SearchUseCase(val tracksRepository: TracksRepository, val artistsRepository: ArtistsRepository) : UseCase<SearchAction, SearchResult> {
    override fun apply(upstream: Observable<SearchAction>): ObservableSource<SearchResult> {
        return upstream
                .debounce(500, TimeUnit.MILLISECONDS)
                .switchMap {
                    Single.zip(
                            tracksRepository.search(it.query),
                            artistsRepository.search(it.query),
                            BiFunction<List<Track>, List<Artist>, SearchResult> { tracks, artists ->
                                SearchResult.Success(tracks, artists)
                            }
                    ).toObservable()
                            .onErrorReturn {
                                SearchResult.Failure(it)
                            }
                            .startWith(SearchResult.InProgress())
                }
    }
}