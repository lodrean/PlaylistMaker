package com.practicum.playlistmaker.player.domain


class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository) :
    AudioPlayerInteractor {
    override fun createAudioPlayer(url: String, listener: PlayerListener) {
        /*playerState = AudioPlayerState.PREPARED*/
        repository.preparePlayer(url, listener)
    }

    override fun play() {
        /*playerState = AudioPlayerState.PLAYING*/
        repository.play()
    }

    override fun pause() {
        repository.pause()
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
