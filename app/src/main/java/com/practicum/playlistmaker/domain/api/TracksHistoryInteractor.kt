package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksHistoryInteractor {
    fun getItems(): MutableList<Track>
    fun clearHistory()
    fun addTrackToHistory(track: Track)
    fun getTrack(): Track
}