package com.practicum.playlistmaker

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
import com.practicum.playlistmaker.SearchActivity.Companion.CHOSEN_TRACK
import com.practicum.playlistmaker.data.dto.Track
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class State {
    DEFAULT,
    PREPARED,
    PLAYING,
    PAUSED
}

class AudioPlayer : AppCompatActivity() {

    companion object {
        private const val PROGRESS_DELAY_MILLIS = 400L
    }

    private var playerState = State.DEFAULT
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
        val extras: Bundle? = intent.extras
        extras?.let {
            val jsonTrack: String? = it.getString(CHOSEN_TRACK)
            track = Json.decodeFromString(jsonTrack!!)
        }
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
            Glide.with(this.applicationContext).load(track?.getCoverArtwork()).fitCenter()
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
        when (playerState) {
            State.PLAYING -> {
                pausePlayer()
            }

            State.PREPARED, State.PAUSED, State.DEFAULT -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track?.url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play?.isEnabled = true
            playerState = State.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play?.setImageResource(R.drawable.play_button)
            playerState = State.PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play?.setImageResource(R.drawable.pause_button)
        playerState = State.PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play?.setImageResource(R.drawable.play_button)
        playerState = State.PAUSED
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
                when (playerState) {
                    State.PLAYING -> {
                        playingProgress?.text = SimpleDateFormat(
                            "mm:ss", Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                        mainThreadHandler?.postDelayed(this, PROGRESS_DELAY_MILLIS)
                    }

                    State.PAUSED -> {
                        mainThreadHandler?.removeCallbacks(this)
                    }

                    State.PREPARED, State.DEFAULT -> {
                        mainThreadHandler?.removeCallbacks(this)
                        playingProgress?.text = getText(R.string.defaultProgressTime).toString()
                    }
                }
            }
        }
    }

}

