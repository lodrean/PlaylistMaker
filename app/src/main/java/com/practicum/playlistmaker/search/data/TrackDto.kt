package com.practicum.playlistmaker.search.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    @SerializedName("trackId") val trackId: Int = 0,
    @SerializedName("trackName") val trackName: String = "",
    @SerializedName("artistName") val artistName: String = "",
    @SerializedName("previewUrl") val url: String = "",
    @SerializedName("trackTimeMillis") val trackTime: Int = 0,
    @SerializedName("artworkUrl100") val artworkUrl100: String = "",
    @SerializedName("collectionName") val collectionName: String = "",
    @SerializedName("releaseDate") val releaseDate: String = "",
    @SerializedName("primaryGenreName") val genre: String = "",
    @SerializedName("country") val country: String = "",
)