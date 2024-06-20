package com.practicum.playlistmaker.new_playlist.data.db

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.new_playlist.data.PlaylistDto
import com.practicum.playlistmaker.new_playlist.domain.Playlist

class PlaylistDbConvertor(private val gson: Gson) {

    fun map(playlisyst: PlaylistDto): PlaylistEntity =
        PlaylistEntity(
            playlisyst.playlistId.toInt(),
            playlisyst.playlistName,
            playlisyst.description,
            playlisyst.imageUri,
            createJsonFromIdList(playlisyst.idList),
            playlisyst.tracksCount,
        )


    fun map(playlist: PlaylistEntity): Playlist =
        Playlist(
            playlist.playlistId.toString(),
            playlist.playlistName,
            playlist.description,
            playlist.imageUri,
            createIdListFromJson(playlist.idList),
            playlist.tracksCount,
        )

    private fun createJsonFromIdList(playlistIds: List<String>): String{
        return gson.toJson(playlistIds)
    }
    private fun createIdListFromJson(json: String): List<String>{
        return if (json != "") {
            gson.fromJson(
                json, object : TypeToken<List<String>>() {}.type
            )
        } else listOf()
    }
}