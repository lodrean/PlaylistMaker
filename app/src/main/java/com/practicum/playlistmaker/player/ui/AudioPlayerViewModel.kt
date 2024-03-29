package com.practicum.playlistmaker.player.ui

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.AudioPlayerState
import com.practicum.playlistmaker.player.domain.PlayerStateListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    application: Application,
    tracksHistoryInteractor: TracksHistoryInteractor,
    private val mediaPlayer: AudioPlayerInteractor
) : AndroidViewModel(application) {

    private val playStatusLiveData = MutableLiveData<PlaybackState>()
    private val progressLiveData = MutableLiveData<String>()



    companion object {
        private const val PROGRESS_DELAY_MILLIS = 400L

        /*fun getViewModelFactory(intent: Intent): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val tracksHistoryInteractor =
                    (this[APPLICATION_KEY] as App).provideTracksHistoryInteractor(
                        intent
                    )
                val track = tracksHistoryInteractor.getTrack()
                val mediaPlayer =
                    (this[APPLICATION_KEY] as App).provideAudioPlayerInteractor()
                AudioPlayerViewModel(
                    this[APPLICATION_KEY] as App,
                    track,
                    mediaPlayer
                )

            }
        }*/
    }

    private val track: Track = tracksHistoryInteractor.getTrack()
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var playerAudioPlayerState = AudioPlayerState.DEFAULT
    private var progressTimer: Runnable = createProgressTimer()


    fun getPlayStatusLiveData(): LiveData<PlaybackState> = playStatusLiveData


    fun observeProgress(): LiveData<String> = progressLiveData

    fun playControl() {
        playbackControl()
        playerAudioPlayerState = mediaPlayer.getPlayerState()
        startProgressTimer()
    }

    fun createAudioPlayer() {

        Log.d("myTag", "track")
        mediaPlayer.createAudioPlayer(track.url, object : PlayerStateListener {
            override fun onPrepared() {
                renderState(PlaybackState.Prepared)
                renderState(PlaybackState.Content(track))
            }

            override fun onCompletion() {
                renderState(PlaybackState.Content(track))
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