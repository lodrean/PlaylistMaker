package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun interface TracksConsumer {
        fun consume(foundTracks: ArrayList<Track>)
    }

}