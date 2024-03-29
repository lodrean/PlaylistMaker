package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.AudioPlayerRepository
import com.practicum.playlistmaker.player.domain.AudioPlayerState
import com.practicum.playlistmaker.player.domain.PlayerStateListener


class AudioPlayerRepositoryImpl(var playerAudioPlayerState: AudioPlayerState, private val mediaPlayer: MediaPlayer) :
    AudioPlayerRepository {



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
            playerAudioPlayerState = AudioPlayerState.PREPARED
        }

    }

    override fun playerStateReporter(): AudioPlayerState {
        return playerAudioPlayerState

    }

    override fun onDestroy() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

}
