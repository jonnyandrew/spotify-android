package com.jonathan_andrew.spotify.ui.search

import com.jonathan_andrew.spotify.R
import com.jonathan_andrew.spotify.domain.entities.exceptions.UnauthorizedException
import com.jonathan_andrew.spotify.domain.use_cases.Action
import com.jonathan_andrew.spotify.domain.use_cases.Result
import com.jonathan_andrew.spotify.domain.use_cases.search.SearchAction
import com.jonathan_andrew.spotify.domain.use_cases.search.SearchResult
import com.jonathan_andrew.spotify.domain.use_cases.search.SearchUseCase
import com.jonathan_andrew.spotify.ui.MviPresenter
import com.jonathan_andrew.spotify.ui.MviView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.merge
import timber.log.Timber

internal class SearchMviPresenter(view: MviView<SearchUiEvent, SearchUiModel>,
                                  private val searchUseCase: SearchUseCase)
    : MviPresenter<SearchUiEvent, SearchUiModel>(view) {

    override val eventTransformer: ObservableTransformer<SearchUiEvent, Action>
        get() = ObservableTransformer { upstream ->
            upstream.publish { shared ->
                listOf<Observable<Action>>(
                        shared.ofType(SearchUiEvent.ChangeSearch::class.java).map { SearchAction(it.search) }
                ).merge()
            }
        }

    override val actionTransformer: ObservableTransformer<Action, Result>
        get() = ObservableTransformer { upstream ->
            upstream.publish { shared ->
                listOf<Observable<Result>>(
                        shared.ofType(SearchAction::class.java).compose(searchUseCase)
                ).merge()
            }
        }

    override val resultTransformer: ObservableTransformer<Result, SearchUiModel>
        get() = ObservableTransformer { upstream ->
            val initialState = SearchUiModel(
                    loading = false,
                    error = false,
                    tracks = listOf(),
                    artists = listOf(),
                    status = R.string.empty_string,
                    loggedOut = false
            )
            upstream.scan(initialState, { state, result ->
                when (result) {
                    is SearchResult.InProgress ->
                        state.copy(
                                error = false,
                                loading = true,
                                status = R.string.search_in_progress
                        )
                    is SearchResult.Success ->
                        state.copy(
                                loading = false,
                                status = R.string.empty_string,
                                tracks = result.tracks.map {
                                    TrackSearchResultUiModel(
                                            title = it.name,
                                            artists = it.artists.joinToString(
                                                    separator = " · ",
                                                    transform = { artist -> artist.name }
                                            )
                                    )
                                },
                                artists = result.artists
                                        .subList(0, minOf(result.artists.size, 3))
                                        .map {
                                            ArtistSearchResultUiModel(
                                                    it.id,
                                                    it.name,
                                                    it.imageUrl
                                            )
                                        }
                        )
                    is SearchResult.Failure ->
                        when (result.throwable) {
                            is UnauthorizedException ->
                                state.copy(loggedOut = true)
                            else -> {
                                Timber.d(result.throwable)
                                state.copy(
                                        error = true,
                                        loading = false,
                                        status = R.string.search_failure
                                )
                            }
                        }
                    else -> state
                }
            })
        }
}
