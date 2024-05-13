package com.practicum.playlistmaker.util

import android.app.Application
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.ThemeSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

const val DARK_THEME_SHARED_PREFERENCES = "practicum_dark_theme_preferences"
const val EDIT_THEME = "dark_theme_enabled"

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        switchTheme(SettingsRepositoryImpl(this).getThemeSettings().isDarkMode)
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        val themeSettings = ThemeSettings(darkThemeEnabled)
        SettingsRepositoryImpl(this).updateThemeSetting(themeSettings)
    }
}
