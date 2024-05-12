package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.SearchState

sealed class PlaybackState(val isPlayButtonEnabled: Boolean, val progress: String) {
    /*object Play : PlaybackState
    object Prepared : PlaybackState
    object Pause : PlaybackState*/


    class Default : PlaybackState(false, "00:00")

    class Prepared : PlaybackState(true, "00:00")

    class Play(progress: String) : PlaybackState(true, progress)

    class Pause(progress: String) : PlaybackState(true, progress)


    data class Content(
        val track: Track
    ) : PlaybackState(true, "00:00")
}