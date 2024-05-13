package com.practicum.playlistmaker.player.domain

interface AudioPlayerInteractor {


    // кнопки play
    fun createAudioPlayer(
        url: String,
        listener: PlayerListener
    )

    fun play()
    fun pause()

    fun destroy()
    fun getCurrentPosition(): Int

    fun onPlay(): Boolean
}