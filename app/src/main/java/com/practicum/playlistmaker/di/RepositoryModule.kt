package com.practicum.playlistmaker.di

import android.content.Intent
import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.AudioPlayerRepository
import com.practicum.playlistmaker.player.domain.AudioPlayerState
import com.practicum.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksHistoryRepository
import com.practicum.playlistmaker.search.domain.TracksRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(AudioPlayerState.DEFAULT, get())
    }

    single<MediaPlayer> {
        MediaPlayer()
    }
    single<TracksHistoryRepository> {(intent: Intent)->
        TracksHistoryRepositoryImpl(androidContext(), intent)
    }
    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }
}