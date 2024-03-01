package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksHistoryRepository {
    fun getItems(): MutableList<Track>
    fun clearHistory()
    fun addTrackToHistory(track: Track)
    fun getTrackFromIntent(): Track
}