package com.practicum.playlistmaker.mediateka.data.db

import android.graphics.Movie
import com.practicum.playlistmaker.mediateka.domain.MediatekaRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MediatekaRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : MediatekaRepository {
    val tracks = appDatabase.trackDao().getTracksAll()
    override fun addToFavorite(track: Track) {
        appDatabase.trackDao().insert(trackDbConvertor.map(track))
    }

    override fun deleteFromFavorite(track: Track) {
        appDatabase.trackDao().delete(trackDbConvertor.map(track))
    }

    override fun mediatekaTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracksAll()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}