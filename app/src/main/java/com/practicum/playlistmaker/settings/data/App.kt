package com.practicum.playlistmaker.settings.data

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val DARK_THEME_SHARED_PREFERENCES = "practicum_dark_theme_preferences"
const val EDIT_THEME = "dark_theme_enabled"

class App : Application() {

    private var darkTheme = false
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(DARK_THEME_SHARED_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(EDIT_THEME, true)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit().putBoolean(EDIT_THEME, darkTheme).apply()
    }
}
