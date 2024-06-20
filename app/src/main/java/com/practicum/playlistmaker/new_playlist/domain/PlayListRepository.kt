package com.practicum.playlistmaker.new_playlist.domain

import android.net.Uri
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlayListRepository {
    fun createPlaylist(playlistName: String, description: String, imageUri: Uri)

    fun getImage(): Uri
    fun saveImage(imageUri: Uri)
    fun getPlaylists(): Flow<List<Playlist>>
    fun addTrackToPlaylist(track: Track, playlist: Playlist): Flow<String>
}
