package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.AudioPlayerRepository
import com.practicum.playlistmaker.player.domain.PlayerListener


class AudioPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : AudioPlayerRepository {


    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun preparePlayer(url: String, listener: PlayerListener) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            listener.onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            listener.onCompletion()
        }

    }


    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun onPlay(): Boolean {
        return mediaPlayer.isPlaying
    }

}
