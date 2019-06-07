package com.jonathan_andrew.spotify.ui

import com.jonathan_andrew.spotify.domain.use_cases.Action
import com.jonathan_andrew.spotify.domain.use_cases.Result
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.lang.ref.WeakReference

abstract internal class MviPresenter<E, M>() {

    private lateinit var viewRef: WeakReference<MviView<E, M>>
    private val subscriptions: CompositeDisposable = CompositeDisposable()
    private val viewSubscriptions = CompositeDisposable()

    private val uiModel = BehaviorSubject.create<M>()

    fun begin(view: MviView<E, M>) {
        viewRef = WeakReference(view)
        viewRef.get()?.let { view ->
            val results = view.events
                    .compose(eventTransformer)
                    .observeOn(Schedulers.io())
                    .doOnNext {
                        Timber.d("action: " + it.toString())
                    }
                    .compose(actionTransformer)
                    .doOnNext {
                        Timber.d("result: " + it.toString())
                    }.share()

            subscriptions.add(
                    results
                            .compose(resultTransformer)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                uiModel.onNext(it)
                            })
        }
    }

    fun resume() {
        viewSubscriptions.add(
                uiModel.subscribe {
                    viewRef.get()?.setUiModel(it)
                }
        )
    }

    fun pause() {
        viewSubscriptions.clear()
    }

    fun end() {
        subscriptions.clear()
    }

    abstract val eventTransformer: ObservableTransformer<E, Action>
    abstract val actionTransformer: ObservableTransformer<Action, Result>
    abstract val resultTransformer: ObservableTransformer<Result, M>
}