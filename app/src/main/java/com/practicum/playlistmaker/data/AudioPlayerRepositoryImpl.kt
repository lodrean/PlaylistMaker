package com.practicum.playlistmaker.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.PlayerStateListener
import com.practicum.playlistmaker.domain.models.AudioPlayerState


class AudioPlayerRepositoryImpl() : AudioPlayerRepository {

    var mediaPlayer = MediaPlayer()
    var playerAudioPlayerState = AudioPlayerState.DEFAULT
    override fun play() {
        mediaPlayer.start()
        playerAudioPlayerState = AudioPlayerState.PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerAudioPlayerState = AudioPlayerState.PAUSED
    }

    override fun preparePlayer(url: String, listener: PlayerStateListener) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            listener.onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            listener.onCompletion()
        }
        playerAudioPlayerState = AudioPlayerState.PREPARED
    }

    override fun playerStateReporter(): AudioPlayerState {
        return playerAudioPlayerState
    }

    override fun onDestroy() {
        mediaPlayer.release()
    }

}
