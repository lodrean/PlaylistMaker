package com.practicum.playlistmaker.playlist.ui

import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track

sealed class PlaylistState {
    data class Content(
        val playlist: Playlist,
        val duration: Int,
        val trackList: List<Track>
    ) : PlaylistState()
}