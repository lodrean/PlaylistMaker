package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.PlayerStateListener
import com.practicum.playlistmaker.domain.models.AudioPlayerState

class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository) :
    AudioPlayerInteractor {
    private var playerState: AudioPlayerState = repository.playerStateReporter()
    override fun createAudioPlayer(url: String, listener: PlayerStateListener) {
        playerState = AudioPlayerState.PREPARED
        repository.preparePlayer(url, listener)
    }

    override fun play() {
        playerState = AudioPlayerState.PLAYING
        repository.play()
    }

    override fun pause() {
        playerState = AudioPlayerState.PAUSED
        repository.pause()
    }

    override fun getPlayerState(): AudioPlayerState {
        return playerState
    }

    override fun destroy() {
        repository.onDestroy()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

}
