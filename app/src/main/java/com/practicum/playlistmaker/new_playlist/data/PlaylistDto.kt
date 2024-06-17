package com.practicum.playlistmaker.new_playlist.data

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    @SerializedName("playlistId") val playlistId: String = "",
    @SerializedName("playlistName") val playlistName: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("imageUri") val imageUri: String = "",
    @SerializedName("idList") var idList: List<String> = listOf(),
    @SerializedName("tracksCount") var tracksCount: Int = 0
)
{
    fun map(playlist: Playlist):PlaylistDto {
        return PlaylistDto(
            playlist.playlistId,
            playlist.playlistName,
            playlist.description,
            playlist.imageUri,
            playlist.idList,
            playlist.tracksCount
        )
    }
    fun map(playlist: PlaylistDto):Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.description,
            playlist.imageUri,
            playlist.idList,
            playlist.tracksCount
        )
    }

}