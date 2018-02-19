package com.jonathan_andrew.spotify.data.artists

import com.jonathan_andrew.spotify.data.util.database.toRealmList
import com.jonathan_andrew.spotify.domain.entities.Artist
import com.jonathan_andrew.spotify.domain.use_cases.artists.ArtistsRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.realm.Realm
import io.realm.kotlin.where

class LocalArtistsRepository : ArtistsRepository {
    override fun search(query: String): Single<List<Artist>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(id: String): Observable<Artist> {
        return Observable.defer {
            Realm.getDefaultInstance().use { realm ->
                val record = realm.where<ArtistRecord>()
                        .equalTo("id", id).findFirst()
                if (record == null) {
                    Observable.empty<Artist>()
                } else {
                    val artist = Mapper.mapToArtist(record)
                    Observable.just(artist)
                }
            }

        }
    }

    override fun put(artist: Artist): Single<Artist> {
        return Single.defer {
            Realm.getDefaultInstance().use { realm ->
                val record = Mapper.mapToArtistRecord(artist)
                realm.executeTransaction {
                    realm.insertOrUpdate(record)
                }
                Single.just(artist)
            }
        }
    }

    private companion object Mapper {
        fun mapToArtist(artistRecord: ArtistRecord): Artist {
            return Artist(
                    id = artistRecord.id,
                    name = artistRecord.name,
                    imageUrl = artistRecord.imageUrl,
                    followers = artistRecord.followers,
                    popularity = artistRecord.popularity,
                    genres = artistRecord.genres.map { it }

            )
        }

        fun mapToArtistRecord(artist: Artist): ArtistRecord {
            return ArtistRecord(
                    id = artist.id,
                    name = artist.name,
                    imageUrl = artist.imageUrl,
                    followers = artist.followers,
                    popularity = artist.popularity,
                    genres = artist.genres.toRealmList()
            )
        }
    }
}