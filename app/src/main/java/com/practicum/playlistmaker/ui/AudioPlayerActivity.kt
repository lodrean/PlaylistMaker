package com.practicum.playlistmaker.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R

import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.domain.api.PlayerStateListener
import com.practicum.playlistmaker.domain.models.AudioPlayerState
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.dpToPx
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AudioPlayer : AppCompatActivity() {

    companion object {
        private const val PROGRESS_DELAY_MILLIS = 400L
    }
    private var playerAudioPlayerState = AudioPlayerState.DEFAULT
    private var track: Track? = Track()
    private var play: ImageView? = null
    private var mediaPlayer = MediaPlayer()
    private var playingProgress: TextView? = null
    private var progressTimer: Runnable? = null
    private var binding: ActivityAudioPlayerBinding? = null
    private var mainThreadHandler: Handler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(LayoutInflater.from(this))
        val view = binding?.root
        setContentView(view)
        val backButton = binding?.backButton
        playingProgress = binding?.tvPlayingProgress
        progressTimer = createProgressTimer()
        backButton?.setOnClickListener { super.finish() }
        track = Creator.getTrack(this.intent)


        mainThreadHandler = Handler(Looper.getMainLooper())

        binding?.tvTrackTitle?.text = track?.trackName
        binding?.tvTrackArtist?.text = track?.artistName
        binding?.tvDurationTime?.text = track?.trackTime?.let { formatMilliseconds(it.toLong()) }
        binding?.tvAlbumName?.text = track?.collectionName
        binding?.tvYearValue?.text =
            track?.releaseDate?.removeRange(4, track!!.releaseDate.lastIndex + 1)
        binding?.tvGenreValue?.text = track?.genre
        binding?.tvCountryValue?.text = track?.country


        val cornerRadius = 8F
        binding?.albumImage?.let {
            Glide.with(this.applicationContext)
                .load(track?.getCoverArtwork()).fitCenter()
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(cornerRadius, applicationContext)))
                .into(it)
        }

        play = binding?.ivPlayButton

        preparePlayer()

        play?.setOnClickListener {
            playbackControl()
            startProgressTimer()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun playbackControl() {
        when (playerAudioPlayerState) {
            AudioPlayerState.PLAYING -> {
                pausePlayer()
                play?.setImageResource(R.drawable.play_button)

            }
            AudioPlayerState.PREPARED, AudioPlayerState.PAUSED, AudioPlayerState.DEFAULT -> {
                startPlayer()
                play?.setImageResource(R.drawable.pause_button)
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track?.url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play?.isEnabled = true
            playerAudioPlayerState = AudioPlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play?.setImageResource(R.drawable.play_button)
            playerAudioPlayerState = AudioPlayerState.PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerAudioPlayerState = AudioPlayerState.PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerAudioPlayerState = AudioPlayerState.PAUSED
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        progressTimer?.let { mainThreadHandler?.removeCallbacks(it) }
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        return format.format(Date(milliseconds))
    }

    private fun startProgressTimer() {
        progressTimer?.let { mainThreadHandler?.post(it) }
    }

    private fun createProgressTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                when (playerAudioPlayerState) {
                    AudioPlayerState.PLAYING -> {
                        playingProgress?.text = SimpleDateFormat(
                            "mm:ss", Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                        mainThreadHandler?.postDelayed(this, PROGRESS_DELAY_MILLIS)
                    }

                    AudioPlayerState.PAUSED -> {
                        mainThreadHandler?.removeCallbacks(this)
                    }

                    AudioPlayerState.PREPARED, AudioPlayerState.DEFAULT -> {
                        mainThreadHandler?.removeCallbacks(this)
                        playingProgress?.text = getText(R.string.defaultProgressTime).toString()
                    }
                }
            }
        }
    }


}

