package com.practicum.playlistmaker.mediateka.data.db

import com.practicum.playlistmaker.mediateka.domain.FavoriteRepository
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.util.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteRepository {

    override suspend fun addToFavorite(track: Track) {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().insert(trackDbConvertor.map(track))
        }
    }

    override suspend fun deleteFromFavorite(track: Track) {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().delete(trackDbConvertor.map(track))
        }
    }

    override fun favoriteTracks(): Flow<List<Track>> = flow {
        var tracks: List<Track>
        withContext(Dispatchers.IO) {
            val tracksEntity = appDatabase.trackDao().getTracksAll()
            tracks = convertFromTrackEntity(tracksEntity)
            tracks.forEach {
                it.isFavorite = true
            }
        }
        emit(tracks)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}
