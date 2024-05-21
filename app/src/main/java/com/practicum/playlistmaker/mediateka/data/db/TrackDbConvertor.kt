package com.practicum.playlistmaker.mediateka.data.db

import android.graphics.Movie
import com.practicum.playlistmaker.search.data.TrackDto
import com.practicum.playlistmaker.search.domain.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.url,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.genre,
            track.country
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.url,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.genre,
            track.country
        )
    }
}