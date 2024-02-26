package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.AudioPlayerState

interface AudioPlayerRepository {

    fun play()
    fun pause()
    fun preparePlayer(url: String, listener: PlayerStateListener)
    fun playerStateReporter(): AudioPlayerState
    fun onDestroy()
}
