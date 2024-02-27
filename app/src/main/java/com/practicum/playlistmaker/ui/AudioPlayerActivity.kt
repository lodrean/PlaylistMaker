package com.practicum.playlistmaker.ui

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
    private var playingProgress: TextView? = null
    private var progressTimer: Runnable? = null
    private var binding: ActivityAudioPlayerBinding? = null
    private var mainThreadHandler: Handler? = null
    private var mediaPlayer = Creator.provideAudioPlayerInteractor()


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
        mediaPlayer.createAudioPlayer(track!!.url, object : PlayerStateListener {
            override fun onPrepared() {
                play?.isEnabled = true
            }

            override fun onCompletion() {
                play?.setImageResource(R.drawable.play_button)
            }
        })
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
            Glide.with(this).load(track?.getCoverArtwork()).fitCenter()
                .dontAnimate().placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(cornerRadius, applicationContext))).into(it)
        }
        play = binding?.ivPlayButton
        play?.setOnClickListener {
            playbackControl()
            playerAudioPlayerState = mediaPlayer.getPlayerState()
            startProgressTimer()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    private fun playbackControl() {
        when (playerAudioPlayerState) {
            AudioPlayerState.PLAYING -> {
                mediaPlayer.pause()
                play?.setImageResource(R.drawable.play_button)
            }

            AudioPlayerState.PREPARED, AudioPlayerState.PAUSED, AudioPlayerState.DEFAULT -> {
                mediaPlayer.play()
                play?.setImageResource(R.drawable.pause_button)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.destroy()
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
                        ).format(mediaPlayer.getCurrentPosition())
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

