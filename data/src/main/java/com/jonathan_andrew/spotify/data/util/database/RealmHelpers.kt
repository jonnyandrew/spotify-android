package com.jonathan_andrew.spotify.data.util.database

import io.realm.RealmList

fun List<String>.toRealmList(): RealmList<String> {
    return RealmList<String>(*this.toTypedArray())
}