package com.practicum.playlistmaker.mediateka.domain

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun addToFavorite(track: Track)

   suspend fun deleteFromFavorite(track: Track)

    fun favoriteTracks(): Flow<List<Track>>
}