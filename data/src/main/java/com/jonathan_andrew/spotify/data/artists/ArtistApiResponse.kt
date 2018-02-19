package com.jonathan_andrew.spotify.data.artists

import com.jonathan_andrew.spotify.data.images.ImageApiResponse

data class ArtistApiResponse(
        val id: String,
        val name: String,
        val images: List<ImageApiResponse> = listOf(),
        val popularity: Int,
        val followers: ArtistFollowersApiResponse,
        val genres: List<String> = listOf()
)

data class ArtistFollowersApiResponse(
        val total: Int
)

