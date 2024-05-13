package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow


interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<ArrayList<Track>>>
}