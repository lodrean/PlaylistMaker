package com.practicum.playlistmaker.domain.api

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.models.Track

interface TracksHistoryRepository {
    fun showSearchHistoryTracks(): List<Track>
}