package com.practicum.playlistmaker.settings.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.ui.SearchState
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.util.App
import java.lang.Appendable

class SettingsViewModel(
    application: Application,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {
    // Основной код

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                val sharingInteractor =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).provideSharingInteractor()

                val settingsInteractor =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).provideSettingsInteractor()

                SettingsViewModel(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App,
                    sharingInteractor,
                    settingsInteractor
                )

            }
        }
    }

    private val settingsLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = settingsLiveData

    fun getThemeSettings(): Boolean {
        return settingsInteractor.getThemeSettings().isDarkMode
    }

    fun updateThemeSettings(checked: Boolean) {
        val themeSettings: ThemeSettings = ThemeSettings(checked)
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