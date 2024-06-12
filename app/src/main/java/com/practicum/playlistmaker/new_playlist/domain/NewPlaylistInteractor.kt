package com.practicum.playlistmaker.new_playlist.domain

import android.net.Uri

interface NewPlaylistInteractor {
    fun createPlaylist(imageUri: Uri, playlistName: String)


}