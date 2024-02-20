package com.practicum.playlistmaker.domain.api

interface MediaPlayerManager {
    fun preparePlayer()

    fun startPlayer()

    fun pausePlayer()

    fun onDestroy()

}