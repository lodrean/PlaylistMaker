package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.search.domain.Track

sealed class PlaybackState(val progress: String) {
    /*object Play : PlaybackState
    object Prepared : PlaybackState
    object Pause : PlaybackState*/


    class Default : PlaybackState("00:00")

    class Prepared : PlaybackState("00:00")

    class Play(progress: String) : PlaybackState(progress)

    class Pause(progress: String) : PlaybackState(progress)


    data class Content(
        val track: Track
    ) : PlaybackState("00:00")
}