package com.practicum.playlistmaker.mediateka.domain.impl

import com.practicum.playlistmaker.mediateka.domain.MediatekaInteractor
import com.practicum.playlistmaker.mediateka.domain.MediatekaRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MediatekaInteractorImpl(private val mediatekaRepository: MediatekaRepository) :
    MediatekaInteractor {
    override fun addToFavorite(track: Track) {
        mediatekaRepository.addToFavorite(track)
    }

    override fun deleteFromFavorite(track: Track) {
        mediatekaRepository.deleteFromFavorite(track)
    }

    override fun mediatekaTracks(): Flow<List<Track>> {
        return mediatekaRepository
            .mediatekaTracks()
            .map { tracks ->
                tracks.reversed()
            }
            }
    }
