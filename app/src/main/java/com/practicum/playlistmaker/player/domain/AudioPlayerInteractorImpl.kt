package com.practicum.playlistmaker.player.domain


class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository) :
    AudioPlayerInteractor {
    private var playerState: AudioPlayerState = repository.playerStateReporter()
    override fun createAudioPlayer(url: String, listener: PlayerStateListener) {
        /*playerState = AudioPlayerState.PREPARED*/
        repository.preparePlayer(url, listener)
    }

    override fun play() {
        /*playerState = AudioPlayerState.PLAYING*/
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

    override fun onPlay(): Boolean {
        return repository.onPlay()
    }

}
