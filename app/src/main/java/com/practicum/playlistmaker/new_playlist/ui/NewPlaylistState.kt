package com.practicum.playlistmaker.new_playlist.ui

sealed interface NewPlaylistState {

    data object Creation : NewPlaylistState
    data class IsCreate(val message: String) : NewPlaylistState

}
