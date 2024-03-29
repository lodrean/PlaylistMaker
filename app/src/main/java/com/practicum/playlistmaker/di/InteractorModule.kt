package com.practicum.playlistmaker.di

import android.content.Intent
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractorImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val interactorModule = module {

    single<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }
    single<TracksHistoryInteractor> {(intent: Intent) ->
        TracksHistoryInteractorImpl(get { parametersOf(intent) })
    }
}