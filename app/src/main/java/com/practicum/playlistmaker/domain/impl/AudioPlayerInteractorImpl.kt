package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.PlayerStateListener
import java.net.URL

class AudioPlayerInteractorImpl(val repository: AudioPlayerRepository) : AudioPlayerInteractor {
    override fun createAudioPlayer(url: String, listener: PlayerStateListener) {
        repository.preparePlayer(url, listener)
    }


    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun getPlayerState() {

    }
}