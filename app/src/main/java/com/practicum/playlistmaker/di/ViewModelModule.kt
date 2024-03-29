package com.practicum.playlistmaker.di

import android.app.Application
import android.content.Intent
import com.practicum.playlistmaker.player.ui.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {(intent: Intent)->
        AudioPlayerViewModel(androidApplication(), get { parametersOf(intent) }, get())
    }

    viewModel { (intent: Intent) ->
        SearchViewModel(androidApplication(), get(), get { parametersOf(intent) })
    }
}