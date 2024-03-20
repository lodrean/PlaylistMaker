package com.practicum.playlistmaker.settings.data

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.ThemeSettings
import com.practicum.playlistmaker.util.App
import com.practicum.playlistmaker.util.DARK_THEME_SHARED_PREFERENCES
import com.practicum.playlistmaker.util.EDIT_THEME

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    lateinit var sharedPrefs: SharedPreferences
    private var darkTheme: Boolean = false
    override fun getThemeSettings(): ThemeSettings {


        sharedPrefs = context.getSharedPreferences(DARK_THEME_SHARED_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(EDIT_THEME, true)
        return ThemeSettings(darkTheme)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {

        sharedPrefs = context.getSharedPreferences(DARK_THEME_SHARED_PREFERENCES, MODE_PRIVATE)
        darkTheme = settings.isDarkMode

        AppCompatDelegate.setDefaultNightMode(
            if (settings.isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit().putBoolean(EDIT_THEME, darkTheme).apply()
    }
}