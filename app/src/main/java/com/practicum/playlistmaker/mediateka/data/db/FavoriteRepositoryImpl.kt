package com.practicum.playlistmaker.mediateka.data.db

import com.practicum.playlistmaker.mediateka.domain.FavoriteRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteRepository {

    override fun addToFavorite(track: Track) {
        appDatabase.trackDao().insert(trackDbConvertor.map(track))
    }

    override fun deleteFromFavorite(track: Track) {
        appDatabase.trackDao().delete(trackDbConvertor.map(track))
    }

    override fun favoriteTracks(): Flow<List<Track>> = flow {
        var tracks: List<TrackEntity>
        withContext(Dispatchers.IO) {
            tracks = appDatabase.trackDao().getTracksAll()
        }
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}