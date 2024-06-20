package com.practicum.playlistmaker.new_playlist.domain.impl

import com.practicum.playlistmaker.new_playlist.domain.PlayListRepository
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.new_playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistInteractorImpl(
    private val playlistRepositrory: PlayListRepository
) : PlaylistInteractor {
      override suspend fun createPlaylist(playlistName: String, description: String, imageUri: String) {
        playlistRepositrory.createPlaylist(playlistName, description, imageUri)
    }



    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepositrory.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Flow<String> {
        return playlistRepositrory.addTrackToPlaylist(track, playlist).map { value ->
            value
        }
    }

}
