package com.practicum.playlistmaker.new_playlist.domain

import android.net.Uri

interface NewPlayListRepository {
    fun createPlaylist(imageUri: String, playlistName: String, description: String)

    fun getImage(): Uri
    fun saveImage(imageUri: String)
}
