package com.practicum.playlistmaker.new_playlist.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.new_playlist.domain.NewPlayListRepository
import com.practicum.playlistmaker.new_playlist.domain.NewPlaylistInteractor

class NewPlaylistInteractorImpl(
    private val newPlaylistRepositrory: NewPlayListRepository
) : NewPlaylistInteractor {
    override fun createPlaylist(imageUri: Uri, playlistName: String) {
        TODO("Not yet implemented")

    }


}
