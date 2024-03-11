package com.practicum.playlistmaker.search.domain

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun interface TracksConsumer {
        fun consume(foundTracks: ArrayList<Track>)
    }

}