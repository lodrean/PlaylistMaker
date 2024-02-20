package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.AudioPlayerState

interface AudioPlayerInteractor {
    fun startPlayer()
    fun pausePlayer()
}