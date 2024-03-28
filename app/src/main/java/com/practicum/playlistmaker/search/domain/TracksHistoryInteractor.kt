package com.practicum.playlistmaker.search.domain


interface TracksHistoryInteractor {
    fun getItems(): MutableList<Track>
    fun clearHistory()
    fun addTrackToHistory(track: Track)
    fun getTrack(): Track
}