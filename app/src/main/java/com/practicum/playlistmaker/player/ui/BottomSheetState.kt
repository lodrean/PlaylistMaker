package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.new_playlist.domain.Playlist

sealed interface BottomSheetState {
    data object Empty : BottomSheetState

    data class Content(
        val playlists: List<Playlist>
    ) : BottomSheetState

    data object InPlaylist: BottomSheetState

    data object AddToPlaylist: BottomSheetState
}