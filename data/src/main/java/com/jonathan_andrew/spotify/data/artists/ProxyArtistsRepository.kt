package com.jonathan_andrew.spotify.data.artists

import com.jonathan_andrew.spotify.domain.entities.Artist
import com.jonathan_andrew.spotify.domain.use_cases.artists.ArtistsRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.merge

/**
 * Responsible for negotiating between the local and remote repositories.
 * Stores remote data in the local repository when it is returned.
 * Returns cached data when remote data isn't available yet.
 */
class ProxyArtistsRepository(
        private val localArtistsRepository: ArtistsRepository,
        private val remoteArtistsRepository: ArtistsRepository
) : ArtistsRepository {
    override fun search(query: String): Single<List<Artist>> {
        return remoteArtistsRepository.search(query)
    }

    override fun get(id: String): Observable<Artist> {
        return Observable.defer {
            val cached = localArtistsRepository.get(id)

            val remote = remoteArtistsRepository.get(id)
                    .flatMapSingle {
                        localArtistsRepository.put(it)
                    }

            listOf(cached, remote).merge()

        }
    }

    override fun put(artist: Artist): Single<Artist> {
        return localArtistsRepository.put(artist)
    }
}