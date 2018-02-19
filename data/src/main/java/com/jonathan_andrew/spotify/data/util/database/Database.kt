package com.jonathan_andrew.spotify.data.util.database

import android.content.Context
import io.realm.Realm

object Database {
    fun init(context: Context) {
        Realm.init(context)
    }
}