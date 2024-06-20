package com.practicum.playlistmaker.new_playlist.domain

import android.net.Uri
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun createPlaylist(playlistName: String, description: String, imageUri: Uri)
    fun saveImage(imageUri: String)
    fun getImage(): Uri
    fun getPlaylists(): Flow<List<Playlist>>
    fun addTrackToPlaylist(track: Track, playlist: Playlist): Flow<String>
}