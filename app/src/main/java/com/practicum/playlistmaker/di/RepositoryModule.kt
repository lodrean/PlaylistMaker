package com.practicum.playlistmaker.di

import android.content.Intent
import android.media.MediaPlayer
import com.practicum.playlistmaker.mediateka.data.db.FavoriteRepositoryImpl
import com.practicum.playlistmaker.mediateka.data.db.TrackDbConvertor
import com.practicum.playlistmaker.mediateka.domain.FavoriteRepository
import com.practicum.playlistmaker.new_playlist.data.NewPlayListRepositoryImpl
import com.practicum.playlistmaker.new_playlist.domain.NewPlayListRepository
import com.practicum.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.AudioPlayerRepository
import com.practicum.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksHistoryRepository
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(get())
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }
    factory<TracksHistoryRepository> { (intent: Intent) ->
        TracksHistoryRepositoryImpl(intent, get(), get(), get())
    }
    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }
    factory<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }
    factory<TrackDbConvertor> {
        TrackDbConvertor()
    }
    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    single<NewPlayListRepository> {
        NewPlayListRepositoryImpl(androidApplication())
    }
}