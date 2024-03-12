package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.res.Configuration
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.ThemeSettings

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {


    override fun getThemeSettings(): ThemeSettings {
        val DarkModeFlags =
            (context as App).resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
        val currentMode = ThemeSettings(isDarkModeOn)
        return currentMode
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        (context as App).switchTheme(settings.isDarkMode)
    }
}