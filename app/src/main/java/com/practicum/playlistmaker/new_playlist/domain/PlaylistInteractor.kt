package com.practicum.playlistmaker.new_playlist.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun createPlaylist(imageUri: String, playlistName: String, description: String)
    fun saveImage(imageUri: String)
    fun getImage(): Uri
    fun getPlaylists(): Flow<List<Playlist>>
}