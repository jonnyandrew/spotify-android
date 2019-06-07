package com.jonathan_andrew.spotify.ui.login

import com.jonathan_andrew.spotify.R
import com.jonathan_andrew.spotify.domain.use_cases.Action
import com.jonathan_andrew.spotify.domain.use_cases.Result
import com.jonathan_andrew.spotify.domain.use_cases.auth.LoginAction
import com.jonathan_andrew.spotify.domain.use_cases.auth.LoginResult
import com.jonathan_andrew.spotify.domain.use_cases.auth.LoginUseCase
import com.jonathan_andrew.spotify.ui.MviPresenter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.merge
import javax.inject.Inject

internal class LoginMviPresenter @Inject constructor(
        private val authUseCase: LoginUseCase
) : MviPresenter<LoginUiEvent, LoginUiModel>() {

    override val eventTransformer: ObservableTransformer<LoginUiEvent, Action>
        get() = ObservableTransformer { upstream ->
            upstream.publish { shared ->
                listOf<Observable<Action>>(
                        shared.ofType(LoginUiEvent.ClickLogin::class.java).map { LoginAction() }
                ).merge()
            }
        }

    override val actionTransformer: ObservableTransformer<Action, Result>
        get() = ObservableTransformer { upstream ->
            upstream.publish { shared ->
                listOf<Observable<Result>>(
                        shared.ofType(LoginAction::class.java).compose(authUseCase)
                ).merge()
            }
        }

    override val resultTransformer: ObservableTransformer<Result, LoginUiModel>
        get() = ObservableTransformer { upstream ->
            val initialState = LoginUiModel(
                    loading = false,
                    message = R.string.empty_string,
                    loggedIn = false
            )
            upstream.scan(initialState, { state, result ->
                when (result) {
                    is LoginResult.InProgress ->
                        state.copy(
                                loading = true,
                                message = R.string.login_in_progress
                        )
                    is LoginResult.Authenticated ->
                        state.copy(
                                loading = true,
                                message = R.string.login_success,
                                loggedIn = true
                        )
                    is LoginResult.Unauthenticated ->
                        state.copy(
                                loading = false,
                                message = R.string.login_failure
                        )
                    else ->
                        state
                }
            })
        }
}
