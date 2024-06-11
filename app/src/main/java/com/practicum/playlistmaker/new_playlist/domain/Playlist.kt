package com.practicum.playlistmaker.new_playlist.domain

data class Playlist(
    val playlistId: String = "",
    val playlistName: String = "",
    val description: String = "",
    val imageUri: String = "",
    val idList: List<String> = listOf(),
    val tracksCount: Int = 0
)
