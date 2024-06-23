package com.practicum.playlistmaker.new_playlist.domain

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlistName: String, description: String, imageUri: String)

    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Flow<String>
    suspend fun getPlaylist(playlistID: String?): Playlist
    suspend fun getTracksByIds(trackIds: List<String>): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: String)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun  getImageUri(uri: String): String
}