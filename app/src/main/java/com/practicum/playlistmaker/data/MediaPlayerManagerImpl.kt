package com.practicum.playlistmaker.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.Json.GetTrackFromJson
import com.practicum.playlistmaker.domain.api.MediaPlayerManager
import com.practicum.playlistmaker.domain.impl.State

class MediaPlayerManagerImpl : MediaPlayerManager {
    val mediaPlayer = MediaPlayer()
    val track = GetTrackFromJson().execute()
    override fun preparePlayer() {
        mediaPlayer.setDataSource(track?.url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play?.isEnabled = true
            playerState = State.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play?.setImageResource(R.drawable.play_button)
            playerState = State.PREPARED
        }
    }
}
