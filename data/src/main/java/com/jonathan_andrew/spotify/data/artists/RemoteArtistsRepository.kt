package com.jonathan_andrew.spotify.data.artists

import com.jonathan_andrew.spotify.data.search.SearchApi
import com.jonathan_andrew.spotify.data.util.network.RemoteRepositoryExceptionHandler
import com.jonathan_andrew.spotify.domain.entities.Artist
import com.jonathan_andrew.spotify.domain.use_cases.artists.ArtistsRepository
import io.reactivex.Observable
import io.reactivex.Single

class RemoteArtistsRepository(val artistApi: ArtistApi, val searchApi: SearchApi) : ArtistsRepository {

    override fun search(query: String): Single<List<Artist>> {
        return searchApi.searchArtists(query)
                .map { searchApiResponse ->
                    searchApiResponse.artists.items.map(this::mapResponseToArtist)
                }
                .onErrorResumeNext(RemoteRepositoryExceptionHandler<List<Artist>>())
    }

    override fun get(id: String): Observable<Artist> {
        return artistApi.get(id)
                .map(this::mapResponseToArtist)
                .onErrorResumeNext(RemoteRepositoryExceptionHandler<Artist>())
                .toObservable()
    }

    override fun put(artist: Artist): Single<Artist> {
        throw NotImplementedError("Cannot put to remote artists repository")
    }

    private fun mapResponseToArtist(response: ArtistApiResponse): Artist {
        return Artist(
                id = response.id,
                name = response.name,
                imageUrl = response.images.maxBy { it.width }?.url,
                followers = response.followers.total,
                popularity = response.popularity,
                genres = response.genres
        )

    }
}