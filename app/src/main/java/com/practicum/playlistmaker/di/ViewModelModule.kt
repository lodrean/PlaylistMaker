package com.practicum.playlistmaker.di

import android.content.Intent
import com.practicum.playlistmaker.mediateka.ui.FavoriteTracksViewModel
import com.practicum.playlistmaker.mediateka.ui.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (intent: Intent) ->
        AudioPlayerViewModel(androidApplication(), get { parametersOf(intent) }, get(), get())
    }

    viewModel { (intent: Intent) ->
        SearchViewModel(androidApplication(), get(), get { parametersOf(intent) })
    }

    viewModel {
        SettingsViewModel(androidApplication(), get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel()
    }
}