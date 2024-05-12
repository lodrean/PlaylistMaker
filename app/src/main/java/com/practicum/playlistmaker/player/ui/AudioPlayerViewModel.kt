package com.practicum.playlistmaker.player.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.AudioPlayerState
import com.practicum.playlistmaker.player.domain.PlayerStateListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    application: Application,
    tracksHistoryInteractor: TracksHistoryInteractor,
    private val mediaPlayer: AudioPlayerInteractor
) : AndroidViewModel(application) {

    private val playStatusLiveData = MutableLiveData<PlaybackState>(PlaybackState.Default())

    /*private val progressLiveData = MutableLiveData<String>()*/
    private var timerJob: Job? = null


    companion object {
        private const val PROGRESS_DELAY_MILLIS = 300L
    }

    private val track: Track = tracksHistoryInteractor.getTrack()
    private var playerAudioPlayerState = AudioPlayerState.DEFAULT
    /*private var progressTimer: Runnable = createProgressTimer()*/


    fun getPlayStatusLiveData(): LiveData<PlaybackState> = playStatusLiveData


    /*fun observeProgress(): LiveData<String> = progressLiveData*/

    fun playControl() {
        playbackControl()
        playerAudioPlayerState = mediaPlayer.getPlayerState()
    }

    fun createAudioPlayer() {


        mediaPlayer.createAudioPlayer(track.url, object : PlayerStateListener {
            override fun onPrepared() {
                renderState(PlaybackState.Prepared())
                renderState(PlaybackState.Content(track))
            }

            override fun onCompletion() {
                renderState(PlaybackState.Content(track))
            }
        })
    }

    /*private fun createProgressTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                when (playerAudioPlayerState) {
                    AudioPlayerState.PLAYING -> {
                        checkProgress(
                            SimpleDateFormat(
                                "mm:ss", Locale.getDefault()
                            ).format(mediaPlayer.getCurrentPosition())
                        )
                        mainThreadHandler.postDelayed(this, PROGRESS_DELAY_MILLIS)
                    }

                    AudioPlayerState.PAUSED -> {
                        mainThreadHandler.removeCallbacks(this)
                    }

                    AudioPlayerState.PREPARED, AudioPlayerState.DEFAULT -> {
                        mainThreadHandler.removeCallbacks(this)
                        checkProgress(
                            getApplication<Application>().getText(R.string.defaultProgressTime)
                                .toString()
                        )
                    }
                }
            }
        }
    }*/
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerAudioPlayerState == AudioPlayerState.PLAYING) {
                delay(PROGRESS_DELAY_MILLIS)
                renderState(PlaybackState.Play(getCurrentPlayerPosition()))
            }
        }
    }
    fun onPause() {
        mediaPlayer.pause()
    }

    private fun playbackControl() {
        when (playerAudioPlayerState) {
            AudioPlayerState.PLAYING -> {
                mediaPlayer.pause()
                timerJob?.cancel()
                renderState(PlaybackState.Play(getCurrentPlayerPosition()))
            }

            AudioPlayerState.PREPARED, AudioPlayerState.PAUSED, AudioPlayerState.DEFAULT -> {
                mediaPlayer.play()

                startTimer()
                renderState(PlaybackState.Pause(getCurrentPlayerPosition()))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.destroy()
        renderState(PlaybackState.Default())
    }


    /*private fun startProgressTimer() {
        progressTimer.let { mainThreadHandler.post(it) }
    }*/

    /*private fun checkProgress(progress: String) {
        progressLiveData.postValue(progress)
    }*/
    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.getCurrentPosition()) ?: "00:00"
    }

    private fun renderState(state: PlaybackState) {
        playStatusLiveData.postValue(state)
    }

}