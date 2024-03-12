package com.practicum.playlistmaker.util

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.AudioPlayerRepository
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksHistoryRepository
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.data.SharingInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context)
            : TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
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

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            context,
            ExternalNavigator(context)
        )
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
}