package com.practicum.playlistmaker.new_playlist.ui

sealed interface NewPlaylistState {

    data class Creation(val creation : Boolean) : NewPlaylistState
    data class IsCreate(val message: String) : NewPlaylistState

}
