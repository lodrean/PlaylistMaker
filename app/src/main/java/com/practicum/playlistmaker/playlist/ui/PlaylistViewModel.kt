package com.practicum.playlistmaker.playlist.ui

import android.app.Application
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.new_playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.util.App
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PlaylistViewModel(
    application: Application,
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : AndroidViewModel(application) {


    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast
    private val playlistLiveData = MutableLiveData<PlaylistState>()
    fun getPlaylistLiveData(): LiveData<PlaylistState> = playlistLiveData
    private var playlist: Playlist = Playlist()

    fun fillData(playlistID: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                playlist = playlistInteractor.getPlaylist(playlistID)
                val trackList = getTracksByIds(playlist.idList)
                val duration = getDurationOfTracklist(trackList)
                renderState(PlaylistState.Content(playlist, duration, trackList))
            }
        }

    }


    private fun renderState(state: PlaylistState) {
        playlistLiveData.postValue(state)
    }

    private suspend fun getTracksByIds(trackIds: List<String>): List<Track> {
        val trackList = mutableListOf<Track>()
        playlistInteractor.getTracksByIds(trackIds).collect { tracks ->
            trackList.addAll(tracks)
        }
        return trackList
    }

    private fun getDurationOfTracklist(trackList: List<Track>): Int {
        var duration = 0L
        for (track in trackList) {
            duration += track.trackTime.toLong()
        }
        return SimpleDateFormat("mm", Locale.getDefault()).format(duration).toInt()
    }

    fun deleteTrack(trackId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                playlistInteractor.deleteTrackFromPlaylist(trackId, playlist.playlistId)
                val trackList = getTracksByIds(playlist.idList)
                val duration = getDurationOfTracklist(trackList)
                renderState(PlaylistState.Content(playlist, duration, trackList))
            }
        }
    }

    fun outOfTracks() {
        showToast(
            getApplication<App>().getString(
                R.string.out_of_tracks_to_share
            )
        )
    }

    fun sharePlaylist() {

        viewModelScope.launch {
            val trackList = getTracksByIds(playlist.idList)
            sharingInteractor.sharePlaylist(playlist, trackList)
        }


    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }

    fun updateFillData() {
        fillData(playlist.playlistId)
    }

    private fun showToast(message: String) {
        showToast.postValue(message)
    }
}