package com.practicum.playlistmaker.domain.api


interface PlayerStateListener {

    fun onPrepared()
    fun onCompletion()

}
