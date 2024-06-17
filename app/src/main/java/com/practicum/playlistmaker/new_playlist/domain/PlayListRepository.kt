package com.practicum.playlistmaker.new_playlist.domain

import android.net.Uri
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlayListRepository {
    fun createPlaylist(imageUri: String, playlistName: String, description: String)

    fun getImage(): Uri
    fun saveImage(imageUri: String)
    fun getPlaylists(): Flow<List<Playlist>>
    fun addTrackToPlaylist(track: Track, playlist: Playlist): Flow<String>
}
