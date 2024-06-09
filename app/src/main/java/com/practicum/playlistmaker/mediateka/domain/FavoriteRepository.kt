package com.practicum.playlistmaker.mediateka.domain

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun addToFavorite(track: Track)

    fun deleteFromFavorite(track: Track)

    fun favoriteTracks(): Flow<List<Track>>
}