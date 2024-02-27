package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.AudioPlayerState

interface AudioPlayerInteractor {


    // кнопки play
    fun createAudioPlayer(
        url: String,
        listener: PlayerStateListener/*, consumer: AudioPlayerConsumer*/
    )

    fun play()
    fun pause()
    fun interface AudioPlayerConsumer {
        fun consume(playerState: AudioPlayerState)
    }

    fun getPlayerState(): AudioPlayerState

    fun destroy()
    fun getCurrentPosition(): Int
}