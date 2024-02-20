package com.practicum.playlistmaker.data.Json

import android.os.Bundle
import com.practicum.playlistmaker.SearchActivity
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.serialization.json.Json

class GetTrackFromJson {
    val track = Track()
    fun execute() {
        val extras: Bundle? = intent.extras
        extras?.let {
            val jsonTrack: String? = it.getString(SearchActivity.CHOSEN_TRACK)
            track = Json.decodeFromString(jsonTrack!!)
        }
    }
}