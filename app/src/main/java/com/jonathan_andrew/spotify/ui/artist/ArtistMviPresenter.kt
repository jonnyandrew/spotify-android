package com.jonathan_andrew.spotify.ui.artist

import com.jonathan_andrew.spotify.R
import com.jonathan_andrew.spotify.domain.entities.Artist
import com.jonathan_andrew.spotify.domain.entities.exceptions.UnauthorizedException
import com.jonathan_andrew.spotify.domain.use_cases.Action
import com.jonathan_andrew.spotify.domain.use_cases.Result
import com.jonathan_andrew.spotify.domain.use_cases.artists.GetArtistAction
import com.jonathan_andrew.spotify.domain.use_cases.artists.GetArtistResult
import com.jonathan_andrew.spotify.domain.use_cases.artists.GetArtistUseCase
import com.jonathan_andrew.spotify.ui.MviPresenter
import com.jonathan_andrew.spotify.ui.MviView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.merge
import timber.log.Timber

internal class ArtistMviPresenter(view: MviView<ArtistUiEvent, ArtistUiModel>,
                                  private val getArtistUseCase: GetArtistUseCase)
    : MviPresenter<ArtistUiEvent, ArtistUiModel>(view) {

    override val eventTransformer: ObservableTransformer<ArtistUiEvent, Action>
        get() = ObservableTransformer { upstream ->
            upstream.publish { shared ->
                listOf<Observable<Action>>(
                        shared.ofType(ArtistUiEvent.Load::class.java).map { GetArtistAction(it.artistId) }
                ).merge()
            }
        }

    override val actionTransformer: ObservableTransformer<Action, Result>
        get() = ObservableTransformer { upstream ->
            upstream.publish { shared ->
                listOf<Observable<Result>>(
                        shared.ofType(GetArtistAction::class.java).compose(getArtistUseCase)
                ).merge()
            }
        }

    override val resultTransformer: ObservableTransformer<Result, ArtistUiModel>
        get() = ObservableTransformer { upstream ->
            val initialState = ArtistUiModel(
                    loading = false,
                    status = R.string.empty_string,
                    error = false,
                    loggedIn = true,
                    name = "",
                    imageUrl = null,
                    followers = 0,
                    genre = "",
                    popularity = R.string.empty_string
            )
            upstream.scan(initialState, { state, result ->
                when (result) {
                    is GetArtistResult.InProgress ->
                        state.copy(
                                loading = true,
                                error = false,
                                status = R.string.artist_in_progress
                        )
                    is GetArtistResult.Success -> {
                        val artist = result.artist
                        state.copy(
                                loading = false,
                                status = R.string.empty_string,
                                name = artist.name,
                                imageUrl = artist.imageUrl,
                                genre = artist.genres.firstOrNull() ?: "",
                                followers = artist.followers,
                                popularity = when (artist.popularityLevel) {
                                    Artist.PopularityLevel.Low -> R.string.artist_popularity_low
                                    Artist.PopularityLevel.Medium -> R.string.artist_popularity_medium
                                    Artist.PopularityLevel.High -> R.string.artist_popularity_high
                                }
                        )
                    }
                    is GetArtistResult.Failure ->
                        when (result.throwable) {
                            is UnauthorizedException ->
                                state.copy(loggedIn = false)
                            else -> {
                                Timber.d(result.throwable)
                                val alreadyLoadedCached = !state.name.isEmpty()
                                state.copy(
                                        error = !alreadyLoadedCached,
                                        loading = false,
                                        status = R.string.artist_failure
                                )
                            }
                        }
                    else -> state
                }
            })
        }
}
