package com.practicum.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<Pair<ArrayList<Track>?, String?>>

}