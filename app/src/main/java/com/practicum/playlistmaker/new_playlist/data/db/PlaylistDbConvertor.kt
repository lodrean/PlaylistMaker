package com.practicum.playlistmaker.new_playlist.data.db

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.new_playlist.domain.Playlist

class PlaylistDbConvertor(private val gson: Gson) {

    fun map(playlisyst: Playlist): PlaylistEntity =
        PlaylistEntity(
            playlisyst.playlistId.toInt(),
            playlisyst.playlistName,
            playlisyst.description,
            playlisyst.imageUri,
            createJsonFromIdList(playlisyst.idList),
            playlisyst.tracksCount,
        )


    fun map(playlisyst: PlaylistEntity): Playlist =
        Playlist(
            playlisyst.playlistId.toString(),
            playlisyst.playlistName,
            playlisyst.description,
            playlisyst.imageUri,
            createIdListFromJson(playlisyst.idList),
            playlisyst.tracksCount,
        )

    private fun createJsonFromIdList(playlistIds: List<String>): String{
        return gson.toJson(playlistIds)
    }
    private fun createIdListFromJson(json: String): List<String>{
        return gson.fromJson(
            json, object : TypeToken<String>() {}.type
        )
    }
}