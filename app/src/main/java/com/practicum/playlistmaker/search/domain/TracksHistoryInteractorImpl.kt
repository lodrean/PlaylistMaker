package com.practicum.playlistmaker.search.domain


class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) :
    TracksHistoryInteractor {
    override fun getItems(): MutableList<Track> {
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