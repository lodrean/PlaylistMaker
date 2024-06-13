package com.practicum.playlistmaker.new_playlist.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.new_playlist.domain.NewPlayListRepository
import com.practicum.playlistmaker.new_playlist.domain.NewPlaylistInteractor

class NewPlaylistInteractorImpl(
    private val newPlaylistRepositrory: NewPlayListRepository
) : NewPlaylistInteractor {
      override fun createPlaylist(imageUri: String, playlistName: String, description: String) {
        newPlaylistRepositrory.createPlaylist(imageUri, playlistName, description)
    }

    override fun saveImage(imageUri: String) {
        newPlaylistRepositrory.saveImage(imageUri)
    }

    override fun getImage(): Uri {
       return newPlaylistRepositrory.getImage()
    }


}
