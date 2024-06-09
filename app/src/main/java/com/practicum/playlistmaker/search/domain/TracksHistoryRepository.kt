package com.practicum.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow

interface TracksHistoryRepository {
    fun getItems(): Flow<MutableList<Track>>
    fun clearHistory()
    fun addTrackToHistory(track: Track)
    fun getTrackFromIntent(): Track
}