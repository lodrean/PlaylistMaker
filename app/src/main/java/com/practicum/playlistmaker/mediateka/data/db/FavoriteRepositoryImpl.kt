package com.practicum.playlistmaker.mediateka.data.db

import com.practicum.playlistmaker.mediateka.domain.FavoriteRepository
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.util.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteRepository {

    override fun addToFavorite(track: Track) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                appDatabase.trackDao().insert(trackDbConvertor.map(track))
            }
        }
    }

    override fun deleteFromFavorite(track: Track) {
        GlobalScope.launch {
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
