package com.practicum.playlistmaker.mediateka.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.new_playlist.domain.PlaylistInteractor

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {


    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    fun getItems(): List<Playlist> {
        return playlistInteractor.getItems()
    }

}