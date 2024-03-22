package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.SearchState

sealed interface PlaybackState {
    object Play : PlaybackState
    object Prepared : PlaybackState
    object Pause : PlaybackState


    data class Content(
        val track: Track
    ) : PlaybackState
}