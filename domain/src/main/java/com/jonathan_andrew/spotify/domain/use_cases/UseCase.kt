package com.jonathan_andrew.spotify.domain.use_cases

import io.reactivex.ObservableTransformer

interface UseCase<A, R> : ObservableTransformer<A, R> where A : Action, R : Result
