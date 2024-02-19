package com.practicum.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.TracksHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksHistoryRepositoryImpl(sharedPreferences: SharedPreferences) : TracksHistoryRepository {
    override fun showSearchHistoryTracks(): List<Track> {
        TODO("Not yet implemented")
    }
}