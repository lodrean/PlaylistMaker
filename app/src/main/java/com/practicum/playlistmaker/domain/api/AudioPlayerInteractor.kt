package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.AudioPlayerState

interface AudioPlayerInteractor {


    // кнопки play
    fun createAudioPlayer(
        url: String,
        listener: PlayerStateListener
    )

    fun play()
    fun pause()

    fun getPlayerState(): AudioPlayerState

    fun destroy()
    fun getCurrentPosition(): Int
}