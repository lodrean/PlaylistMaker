package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val trackId: Int,
    val trackName: String?,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    @SerializedName("primaryGenreName") val genre: String,
    val country: String,
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
