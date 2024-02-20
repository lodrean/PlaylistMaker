package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.MediaPlayerManager
import com.practicum.playlistmaker.domain.models.AudioPlayerState

class AudioPlayerInteractorImpl(val mediaPlayer: MediaPlayerManager) : AudioPlayerInteractor {
    // Реализация методов для работы с воспроизведением здесь


    var playerState = AudioPlayerState.DEFAULT


    override fun startPlayer() {
        mediaPlayer.start()
        playerState = AudioPlayerState.PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = AudioPlayerState.PAUSED
    }


}



