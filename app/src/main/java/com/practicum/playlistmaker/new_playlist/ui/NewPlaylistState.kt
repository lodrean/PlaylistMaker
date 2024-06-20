package com.practicum.playlistmaker.new_playlist.ui

import android.net.Uri

sealed interface NewPlaylistState {

    data class Creation(val creation : Boolean,
        val uri: Uri
    ) : NewPlaylistState
    data class IsCreate(val message: String) : NewPlaylistState

}
