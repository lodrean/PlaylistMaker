package com.practicum.playlistmaker.sharing.domain

import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track


interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun sharePlaylist(playlist: Playlist, tracklist: List<Track>)
}