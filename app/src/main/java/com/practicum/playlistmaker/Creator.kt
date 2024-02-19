package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(sharedPreferences: SharedPreferences)
            : TracksInteractor {
        return TracksInteractorImpl(
            repository = TracksRepositoryImpl(RetrofitNetworkClient()),
            history = TracksHistoryRepositoryImpl(sharedPreferences)
        )
    }
}