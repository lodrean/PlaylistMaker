package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.data.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.data.GetTrackFromIntentUseCaseImpl
import com.practicum.playlistmaker.data.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.GetTrackFromIntentUseCase
import com.practicum.playlistmaker.domain.api.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksHistoryRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.models.Track

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor()
            : TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getTracksHistoryRepository(context: Context): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(context)
    }

    fun provideTracksHistoryInteractor(context: Context): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository(context))
    }

    private fun getTrackFromIntentUseCase(intent: Intent): GetTrackFromIntentUseCase {
        return GetTrackFromIntentUseCaseImpl(intent)
    }

    fun getTrack(intent: Intent): Track {
        return getTrackFromIntentUseCase(intent).execute()
    }

    private fun getAudioRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor()
            : AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioRepository())
    }
}