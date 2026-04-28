package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTracksInteractor : TracksInteractor {

    var result: Pair<ArrayList<Track>?, String?> = Pair(null, null)

    override fun searchTracks(expression: String): Flow<Pair<ArrayList<Track>?, String?>> = flow {
        emit(result)
    }
}
