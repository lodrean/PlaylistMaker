package com.practicum.playlistmaker.playlist.ui

import com.practicum.playlistmaker.new_playlist.domain.Playlist


sealed interface RedactPlaylistState {

    data class Content(
        val playlist: Playlist
    ): RedactPlaylistState
}