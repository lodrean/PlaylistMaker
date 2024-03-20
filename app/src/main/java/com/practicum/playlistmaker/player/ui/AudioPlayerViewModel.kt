package com.practicum.playlistmaker.player.ui

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.AudioPlayerState
import com.practicum.playlistmaker.player.domain.PlayerStateListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.util.App
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioPlayerViewModel(
    application: Application,
    private val tracksHistoryInteractor: TracksHistoryInteractor,
    private val mediaPlayer: AudioPlayerInteractor
) : AndroidViewModel(application) {

    companion object {
        private const val PROGRESS_DELAY_MILLIS = 400L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val tracksHistoryInteractor =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).provideTracksHistoryInteractor(
                        Intent()
                    )
                val track = tracksHistoryInteractor.getTrack()
                val mediaPlayer =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).provideAudioPlayerInteractor()
                AudioPlayerViewModel(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App,
                    tracksHistoryInteractor,
                    mediaPlayer
                )

            }
        }
    }


    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var playerAudioPlayerState = AudioPlayerState.DEFAULT
    private var progressTimer: Runnable = createProgressTimer()


    private val playStatusLiveData = MutableLiveData<PlaybackState>()
    fun getPlayStatusLiveData(): LiveData<PlaybackState> = playStatusLiveData

    private val progressLiveData = MutableLiveData<String>()

    fun observeProgress(): LiveData<String> = progressLiveData
    fun createAudioPlayer() {
        val track = tracksHistoryInteractor.getTrack()
        mediaPlayer.createAudioPlayer(track.url, object : PlayerStateListener {
            override fun onPrepared() {
                renderState(PlaybackState.Prepared)
            }

            override fun onCompletion() {
                renderState(PlaybackState.Pause)
            }
        })
    }

    private fun createProgressTimer(): Runnable {
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
    }

    fun onPause() {
        mediaPlayer.pause()
    }

    private fun playbackControl() {
        when (playerAudioPlayerState) {
            AudioPlayerState.PLAYING -> {
                mediaPlayer.pause()
                renderState(PlaybackState.Play)
            }

            AudioPlayerState.PREPARED, AudioPlayerState.PAUSED, AudioPlayerState.DEFAULT -> {
                mediaPlayer.play()
                renderState(PlaybackState.Pause)
            }
        }
    }

    fun onDestroy() {
        mediaPlayer.destroy()
        progressTimer.let { mainThreadHandler.removeCallbacks(it) }
    }


    private fun startProgressTimer() {
        progressTimer.let { mainThreadHandler.post(it) }
    }

    private fun checkProgress(progress: String) {
        progressLiveData.postValue(progress)
    }


    private fun renderState(state: PlaybackState) {
        playStatusLiveData.postValue(state)
    }

}