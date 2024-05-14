package com.practicum.playlistmaker.player.domain

interface AudioPlayerRepository {

    fun play()
    fun pause()
    fun preparePlayer(url: String, listener: PlayerListener)
    fun onDestroy()
    fun getCurrentPosition(): Int

    fun onPlay(): Boolean
}
