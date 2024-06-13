package com.practicum.playlistmaker.new_playlist.domain

import android.net.Uri

interface NewPlaylistInteractor {
    fun createPlaylist(imageUri: String, playlistName: String, description: String)
    fun saveImage(imageUri: String)
    fun getImage(): Uri
}