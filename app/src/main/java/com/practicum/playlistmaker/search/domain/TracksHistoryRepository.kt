package com.practicum.playlistmaker.search.domain

interface TracksHistoryRepository {
    fun getItems(): MutableList<Track>
    fun clearHistory()
    fun addTrackToHistory(track: Track)
    fun getTrackFromIntent(): Track
}