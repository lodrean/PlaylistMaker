package com.practicum.playlistmaker.util

import android.app.Application
import android.content.Intent
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.search.data.ItunesApiService
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.domain.ThemeSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val DARK_THEME_SHARED_PREFERENCES = "practicum_dark_theme_preferences"
const val EDIT_THEME = "dark_theme_enabled"

class App : Application() {

    fun provideSettingsInteractor() = Creator.provideSettingsInteractor(this)
    fun provideSharingInteractor() = Creator.provideSharingInteractor()
    private fun getRepository(): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(
                Retrofit.Builder()
                    .baseUrl("https://itunes.apple.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ItunesApiService::class.java), this
            )
        )
    }
    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getRepository())
    }
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        switchTheme(provideSettingsInteractor().getThemeSettings().isDarkMode)
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val themeSettings = ThemeSettings(darkThemeEnabled)
        provideSettingsInteractor().updateThemeSetting(themeSettings)
    }

    fun provideTracksHistoryInteractor(intent: Intent): TracksHistoryInteractor {
        return Creator.provideTracksHistoryInteractor(intent)
    }


}
