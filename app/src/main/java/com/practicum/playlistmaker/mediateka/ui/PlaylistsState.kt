package com.practicum.playlistmaker.mediateka.ui

import com.practicum.playlistmaker.new_playlist.domain.Playlist

sealed interface PlaylistsState {

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsState

    data object Empty : PlaylistsState
}