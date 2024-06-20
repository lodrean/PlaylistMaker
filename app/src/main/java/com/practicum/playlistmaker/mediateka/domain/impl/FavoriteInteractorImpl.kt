package com.practicum.playlistmaker.mediateka.domain.impl

import com.practicum.playlistmaker.mediateka.domain.FavoriteInteractor
import com.practicum.playlistmaker.mediateka.domain.FavoriteRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository) :
    FavoriteInteractor {
    override suspend fun addToFavorite(track: Track) {
        favoriteRepository.addToFavorite(track)

    }

  override suspend fun deleteFromFavorite(track: Track) {
        favoriteRepository.deleteFromFavorite(track)

    }

    override fun favoriteTracks(): Flow<List<Track>> {
        return favoriteRepository
            .favoriteTracks()
            .map { tracks -> tracks.reversed() }
    }
}
