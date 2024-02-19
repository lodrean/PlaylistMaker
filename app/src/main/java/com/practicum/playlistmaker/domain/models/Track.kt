package com.practicum.playlistmaker.domain.models

import com.google.gson.annotations.SerializedName

data class Track(
    val trackId: Int = 0,
    val trackName: String? = "",
    val artistName: String = "",
    val url: String = "",
    val trackTime: String = "",
    val artworkUrl100: String = "",
    val collectionName: String = "",
    val releaseDate: String = "",
    val genre: String = "",
    val country: String = "",
)