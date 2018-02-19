package com.jonathan_andrew.spotify.data.tracks

import com.jonathan_andrew.spotify.data.search.SearchApi
import com.jonathan_andrew.spotify.data.util.network.RemoteRepositoryExceptionHandler
import com.jonathan_andrew.spotify.domain.entities.Artist
import com.jonathan_andrew.spotify.domain.entities.Track
import com.jonathan_andrew.spotify.domain.use_cases.tracks.TracksRepository
import io.reactivex.Single

class RemoteTracksRepository(private val searchApi: SearchApi) : TracksRepository {
    override fun search(query: String): Single<List<Track>> {
        return searchApi.searchTracks(query)
                .map { searchApiResponse ->
                    searchApiResponse.tracks.items.map {
                        Track(
                                it.id,
                                it.name,
                                it.artists.map {
                                    Artist(
                                            it.id,
                                            it.name,
                                            imageUrl = null,
                                            followers = 0,
                                            popularity = 0,
                                            genres = listOf()
                                    )
                                }

                        )
                    }
                }
                .onErrorResumeNext(RemoteRepositoryExceptionHandler<List<Track>>())
    }

}