package com.practicum.playlistmaker.settings.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.search.ui.SearchState
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    application: Application,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {
    // Основной код

    private val settingsLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = settingsLiveData

    fun getThemeSettings(): Boolean {
        return settingsInteractor.getThemeSettings().isDarkMode
    }

    fun updateThemeSettings(checked: Boolean) {
        val themeSettings = ThemeSettings(checked)
        settingsInteractor.updateThemeSetting(themeSettings)
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
}