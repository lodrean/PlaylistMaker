package com.practicum.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.MediaPlayerManager

class AudioPlayerInteractor(private val mediaPlayerManger: MediaPlayerManager) {
    // Реализация методов для работы с воспроизведением здесь


    val mediaPlayer = MediaPlayer()
    val playerState = State.DEFAULT


    private fun startPlayer() {
        mediaPlayer.start()
        play?.setImageResource(R.drawable.pause_button)
        playerState = State.PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play?.setImageResource(R.drawable.play_button)
        playerState = State.PAUSED
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        progressTimer?.let { mainThreadHandler?.removeCallbacks(it) }
    }
}

enum class State {
    DEFAULT,
    PREPARED,
    PLAYING,
    PAUSED
}