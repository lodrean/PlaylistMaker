package com.practicum.playlistmaker.new_playlist.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,
    val playlistName: String,
    val description: String,
    val imageUri: String,
    val idList: String,
    val tracksCount: Int = 0
)