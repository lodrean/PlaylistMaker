package com.practicum.playlistmaker.search.domain


interface TracksRepository {
    fun searchTracks(expression: String): ArrayList<Track>
}