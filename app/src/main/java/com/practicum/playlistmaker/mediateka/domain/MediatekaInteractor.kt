package com.practicum.playlistmaker.mediateka.domain

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface MediatekaInteractor {
    fun addToFavorite(track: Track)

    fun deleteFromFavorite(track: Track)

    fun mediatekaTracks(): Flow<List<Track>>
}