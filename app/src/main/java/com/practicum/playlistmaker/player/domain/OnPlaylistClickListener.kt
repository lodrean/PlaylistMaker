package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.new_playlist.domain.Playlist


fun interface OnPlaylistClickListener {
    fun onItemClick(playlist: Playlist)
}
