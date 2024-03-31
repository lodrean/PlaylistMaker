package com.practicum.playlistmaker.di

import android.content.Intent
import android.util.Log
import com.practicum.playlistmaker.player.ui.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.Constant
import com.practicum.playlistmaker.search.ui.SearchViewModel
import org.koin.android.ext.koin.androidApplication
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