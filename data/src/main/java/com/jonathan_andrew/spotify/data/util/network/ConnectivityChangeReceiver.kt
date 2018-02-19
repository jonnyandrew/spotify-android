package com.jonathan_andrew.spotify.data.util.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ConnectivityChangeReceiver : BroadcastReceiver() {
    private val networkStatesSubject: PublishSubject<Boolean> = PublishSubject.create()

    val networkStates: Observable<Boolean> = networkStatesSubject

    override fun onReceive(context: Context, intent: Intent) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        val connected = activeNetwork != null && activeNetwork.isConnected
        networkStatesSubject.onNext(connected)
    }
}
