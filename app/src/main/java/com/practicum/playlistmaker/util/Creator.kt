package com.practicum.playlistmaker.util

import android.content.Context
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

object Creator {
    private lateinit var application: App
    /*private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(application))
    }*/

    fun init(app: App) {
        application = app
    }


    /*private fun getTracksHistoryRepository(
        context: Context,
        intent: Intent,
    ): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(context, intent)
    }*/
    /*fun provideTracksHistoryInteractor(intent: Intent): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository(application, intent))
    }*/

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(
            application,
            ExternalNavigator(application)
        )
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
}