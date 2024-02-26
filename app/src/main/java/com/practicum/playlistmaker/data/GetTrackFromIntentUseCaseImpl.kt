package com.practicum.playlistmaker.data

import android.content.Intent
import android.os.Bundle
import com.practicum.playlistmaker.domain.api.GetTrackFromIntentUseCase
import com.practicum.playlistmaker.domain.models.Constant.Companion.CHOSEN_TRACK
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.serialization.json.Json

class GetTrackFromIntentUseCaseImpl(intent: Intent) : GetTrackFromIntentUseCase {
    private val intent = intent
    override fun execute(): Track {
        val extras: Bundle? = intent.extras
        val jsonTrack: String? = extras?.getString(CHOSEN_TRACK)
        return Json.decodeFromString(jsonTrack!!)
    }

}