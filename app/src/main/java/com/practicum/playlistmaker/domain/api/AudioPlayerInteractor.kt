package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.AudioPlayerState
import com.practicum.playlistmaker.domain.models.Track
import java.net.URL

interface AudioPlayerInteractor {
    // кнопки play
    fun createAudioPlayer(url: String, listener: PlayerStateListener)
    fun play()
    fun pause()
    fun interface AudioPlayerConsumer {
        fun consume(playerState: AudioPlayerState)
    }

    fun getPlayerState(

    )

    fun destroy() {

    }
}