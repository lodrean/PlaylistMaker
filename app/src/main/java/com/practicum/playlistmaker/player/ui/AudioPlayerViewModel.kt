package com.practicum.playlistmaker.player.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.FavoriteInteractor
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.new_playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    application: Application,
    tracksHistoryInteractor: TracksHistoryInteractor,
    private val mediaPlayer: AudioPlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {
    private val showToast = SingleLiveEvent<String>()
    private val playStatusLiveData = MutableLiveData<PlaybackState>(PlaybackState.Default())
    private var timerJob: Job? = null
    private val bottomSheetLiveData = MutableLiveData<BottomSheetState>()

    companion object {
        private const val PROGRESS_DELAY_MILLIS = 300L
    }

    private val track: Track = tracksHistoryInteractor.getTrack()
    fun getPlayStatusLiveData(): LiveData<PlaybackState> = playStatusLiveData
    fun getBottomSheetLiveData(): LiveData<BottomSheetState> = bottomSheetLiveData
    fun observeShowToast(): LiveData<String> = showToast
    fun playControl() {
        playbackControl()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val isTrackFavorite = track.isFavorite
                if (!track.isFavorite) {
                    favoriteInteractor.addToFavorite(track)
                } else {
                    favoriteInteractor.deleteFromFavorite(track)
                }
                track.isFavorite = !isTrackFavorite
                renderState(
                    PlaybackState.Content(track)
                )
            }
        }
    }

    fun fillData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                playlistInteractor
                    .getPlaylists()
                    .collect { playlists ->
                        processResult(playlists)
                    }
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderList(BottomSheetState.Empty)
        } else {
            renderList(BottomSheetState.Content(playlists))
        }
    }

    private fun renderList(state: BottomSheetState) {
        bottomSheetLiveData.postValue(state)
    }

    fun createAudioPlayer() {
        mediaPlayer.createAudioPlayer(track.url, object : PlayerListener {
            override fun onPrepared() {
                renderState(PlaybackState.Content(track))
            }

            override fun onCompletion() {
                renderState(PlaybackState.Content(track))
            }
        })
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.onPlay()) {
                delay(PROGRESS_DELAY_MILLIS)
                renderState(PlaybackState.Play(getCurrentPlayerPosition()))
            }
            timerJob?.cancel()
            renderState(PlaybackState.Prepared())
        }
    }

    fun onPause() {
        pausePlayer()
    }

    private fun playbackControl() {
        when (playStatusLiveData.value) {
            is PlaybackState.Play -> {
                pausePlayer()
            }

            else
            -> {
                mediaPlayer.play()
                startTimer()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.destroy()
        playStatusLiveData.value = PlaybackState.Default()
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.getCurrentPosition()) ?: "00:00"
    }

    private fun renderState(state: PlaybackState) {
        playStatusLiveData.postValue(state)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        renderState(PlaybackState.Pause(getCurrentPlayerPosition()))
    }

    private fun showToast(message: String) {
        showToast.postValue(message)
    }

    fun addToPlaylist(playlist: Playlist) {
        if (track.trackId in playlist.idList) {
            showToast("Трек уже добавлен в плейлист ${playlist.playlistName}")
            renderList(BottomSheetState.InPlaylist)
        } else {
            viewModelScope.launch {
                playlistInteractor
                    .addTrackToPlaylist(track, playlist)
                    .collect { message ->
                        showToast(message)
                    }
            }
            renderList(BottomSheetState.AddToPlaylist)
        }
        fillData()
    }

}