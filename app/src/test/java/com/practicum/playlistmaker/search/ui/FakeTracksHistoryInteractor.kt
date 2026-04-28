package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTracksHistoryInteractor : TracksHistoryInteractor {

    private var history: List<Track> = emptyList()

    override fun getItems(): Flow<MutableList<Track>> = flow {
        emit(history.toMutableList())
    }

    override fun clearHistory() {
        history = emptyList()
    }

    override suspend fun addTrackToHistory(track: Track) {
        val current = history.toMutableList()
        current.removeAll { it.trackId == track.trackId }
        current.add(0, track)
        history = current
    }

    override fun getTrack(): Track {
        return history.firstOrNull() ?: Track()
    }

    fun setHistory(tracks: List<Track>) {
        history = tracks
    }
}
