package com.practicum.playlistmaker.new_playlist.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    @SerializedName("playlistId") val playlistId: String = "",
    @SerializedName("playlistName") val playlistName: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("imageUri") val imageUri: String = "",
    @SerializedName("idList") val idList: List<String> = listOf(),
    @SerializedName("tracksCount") val tracksCount: Int = 0
)