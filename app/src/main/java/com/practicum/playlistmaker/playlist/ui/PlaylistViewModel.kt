package com.practicum.playlistmaker.playlist.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.new_playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch

class PlaylistViewModel(
    application: Application,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {


    private val playlist = playlistInteractor.getPlaylist()

    private fun getTrackList(): List<Track> {
        val trackList = mutableListOf<Track>()
        viewModelScope.launch {
            playlistInteractor.getTracksByIds(playlist.idList)
                .collect { tracks ->
                    trackList.addAll(tracks)
                }
        }
        return trackList
    }

}