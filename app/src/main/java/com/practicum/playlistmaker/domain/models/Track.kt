package com.practicum.playlistmaker.domain.models

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val url: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val genre: String,
    val country: String,
)