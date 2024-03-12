package com.practicum.playlistmaker.settings.data

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.ThemeSettings

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    val DarkModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
    override fun getThemeSettings(): ThemeSettings {

        return ThemeSettings(isDarkModeOn)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        (context as App).switchTheme(settings.isChecked)
    }
}