package com.practicum.playlistmaker.new_playlist.domain.impl

import com.practicum.playlistmaker.new_playlist.domain.NewPlayListRepository
import com.practicum.playlistmaker.new_playlist.domain.NewPlaylistInteractor
import com.practicum.playlistmaker.new_playlist.domain.Playlist

class NewPlaylistInteractorImpl(
    private val newPlaylistRepositrory: NewPlayListRepository
) : NewPlaylistInteractor {
    override fun createPlaylist(playlist: Playlist) {

    }
}