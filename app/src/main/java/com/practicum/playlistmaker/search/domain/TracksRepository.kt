package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.util.Resource


interface TracksRepository {
    fun searchTracks(expression: String): Resource<ArrayList<Track>>
}