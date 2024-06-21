package com.practicum.playlistmaker.playlist.ui

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.new_playlist.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    application: Application,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {

    private val playlistId =

    fun getPlaylist(){}
}