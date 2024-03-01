package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.data.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.data.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksHistoryRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor()
            : TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getTracksHistoryRepository(
        context: Context,
        intent: Intent,
    ): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(context, intent)
    }

    fun provideTracksHistoryInteractor(context: Context, intent: Intent): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository(context, intent))
    }


    private fun getAudioRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor()
            : AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioRepository())
    }
}