package com.practicum.playlistmaker.util

import android.app.Application
import android.content.Intent
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.domain.ThemeSettings

const val DARK_THEME_SHARED_PREFERENCES = "practicum_dark_theme_preferences"
const val EDIT_THEME = "dark_theme_enabled"

class App : Application() {

    fun provideSettingsInteractor() = Creator.provideSettingsInteractor(this)
    fun provideSharingInteractor() = Creator.provideSharingInteractor()
    private fun getRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(this))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getRepository())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return Creator.provideAudioPlayerInteractor()
    }
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        switchTheme(provideSettingsInteractor().getThemeSettings().isDarkMode)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val themeSettings = ThemeSettings(darkThemeEnabled)
        provideSettingsInteractor().updateThemeSetting(themeSettings)
    }

    fun provideTracksHistoryInteractor(intent: Intent): TracksHistoryInteractor {
        return Creator.provideTracksHistoryInteractor(intent)
    }


}
