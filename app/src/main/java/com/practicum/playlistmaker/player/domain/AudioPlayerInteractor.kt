package com.practicum.playlistmaker.player.domain

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

    fun onPlay(): Boolean
}