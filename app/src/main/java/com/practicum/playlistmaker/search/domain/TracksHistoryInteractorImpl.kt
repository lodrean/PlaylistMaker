package com.practicum.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow


class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) :
    TracksHistoryInteractor {
    override fun getItems(): Flow<MutableList<Track>> {
        return repository.getItems()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun getTrack(): Track {
        return repository.getTrackFromIntent()
    }
}