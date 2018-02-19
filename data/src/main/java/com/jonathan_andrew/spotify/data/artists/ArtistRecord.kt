package com.jonathan_andrew.spotify.data.artists

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ArtistRecord(
        @PrimaryKey
        var id: String = "",
        var name: String = "",
        var imageUrl: String? = null,
        var followers: Int = -1,
        var popularity: Int = -1,
        var genres: RealmList<String> = RealmList()

) : RealmObject()
