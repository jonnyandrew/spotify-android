package com.jonathan_andrew.spotify.domain.entities

data class Artist(
        val id: String,
        val name: String,
        val imageUrl: String?,
        val followers: Int,
        val popularity: Int,
        val genres: List<String>
) {

    init {
        if (popularity !in 0..100) {
            throw IllegalStateException("Popularity is out of range")
        }
    }

    val popularityLevel = when (popularity) {
        in 0..33 -> PopularityLevel.Low
        in 33..66 -> PopularityLevel.Medium
        in 66..100 -> PopularityLevel.High
        else -> throw IllegalStateException("Popularity is out of range")
    }

    enum class PopularityLevel {
        High, Medium, Low
    }
}
