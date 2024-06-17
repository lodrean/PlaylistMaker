package com.practicum.playlistmaker.new_playlist.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.new_playlist.domain.PlayListRepository
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.new_playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepositrory: PlayListRepository
) : PlaylistInteractor {
      override fun createPlaylist(imageUri: String, playlistName: String, description: String) {
        playlistRepositrory.createPlaylist(imageUri, playlistName, description)
    }

    override fun saveImage(imageUri: String) {
        playlistRepositrory.saveImage(imageUri)
    }

    override fun getImage(): Uri {
       return playlistRepositrory.getImage()
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepositrory.getPlaylists()
    }

    override fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepositrory.addTrackToPlaylist(track, playlist)
    }

}
