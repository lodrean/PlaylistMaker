package com.practicum.playlistmaker.di

import android.content.Intent
import android.util.Log
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.Constant
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val interactorModule = module {

    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }
    factory<TracksHistoryInteractor> { (intent: Intent) ->
        Log.d("rep2", "intent = " + intent.extras?.getString(Constant.CHOSEN_TRACK))
        TracksHistoryInteractorImpl(get { parametersOf(intent) })
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }
}