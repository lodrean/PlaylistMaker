package com.practicum.playlistmaker.mediateka.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey val trackId: String,
    val trackName: String,
    val artistName: String,
    val url: String,
    val trackTime: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val genre: String,
    val country: String,
)