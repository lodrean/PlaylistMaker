package com.practicum.playlistmaker.search.domain
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val trackId: Int = 0,
    val trackName: String = "",
    val artistName: String = "",
    val url: String = "",
    val trackTime: Int = 0,
    val artworkUrl100: String = "",
    val collectionName: String = "",
    val releaseDate: String = "",
    val genre: String = "",
    val country: String = "",
    var isFavorite: Boolean = false
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
