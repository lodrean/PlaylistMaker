package com.practicum.playlistmaker.playlist.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.new_playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.new_playlist.ui.NewPlayLIstViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RedactPlaylistViewModel(
    application: Application,
    private val playlistID: String,
    private val playlistInteractor: PlaylistInteractor
) : NewPlayLIstViewModel(application, playlistInteractor) {

    private val playlistLiveData = MutableLiveData<RedactPlaylistState>()
    fun getPlaylistLiveData(): LiveData<RedactPlaylistState> = playlistLiveData


    fun fillData() {
        viewModelScope.launch {
            val playlist: Playlist = getPlaylist(playlistID)
            playlistLiveData.postValue(RedactPlaylistState.Content(playlist))
        }
    }


    private suspend fun getPlaylist(playlistID: String): Playlist {

        return playlistInteractor.getPlaylist(playlistID)
    }

    fun updatePlaylist(playlistName: String, playlistDescription: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val playlist = getPlaylist(playlistID)
                val newPlaylist = Playlist(
                    playlistID,
                    playlistName,
                    playlistDescription,
                    getImageUri().toString(),
                    playlist.idList,
                    playlist.tracksCount
                )
                playlistInteractor.updatePlaylist(newPlaylist)


            }
        }
    }
}