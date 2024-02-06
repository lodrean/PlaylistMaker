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
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioPlayer : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val PROGRESS_DELAY = 400L
    }

    private var playerState = STATE_DEFAULT
    private lateinit var track: Track
    private lateinit var play: ImageView
    private var mediaPlayer = MediaPlayer()
    private lateinit var playingProgress: TextView
    private lateinit var progressTimer: Runnable
    private lateinit var binding: ActivityAudioPlayerBinding
    private var mainThreadHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)
        val backButton = binding.backButton
        playingProgress = binding.tvPlayingProgress
        progressTimer = createProgressTimer()
        backButton.setOnClickListener { super.finish() }
        track = Track(0, "", "", "", "", "", "", "", "", "")
        val extras: Bundle? = intent.extras
        extras?.let {
            val jsonTrack: String? = it.getString(CHOSEN_TRACK)
            track = Json.decodeFromString(jsonTrack!!)
        }
        mainThreadHandler = Handler(Looper.getMainLooper())

        binding.tvTrackTitle.text = track.trackName
        binding.tvTrackArtist.text = track.artistName
        binding.tvDurationTime.text = formatMilliseconds(track.trackTime.toLong())
        binding.tvAlbumName.text = track.collectionName
        binding.tvYearValue.text = track.releaseDate.removeRange(4, track.releaseDate.lastIndex + 1)
        binding.tvGenreValue.text = track.genre
        binding.tvCountryValue.text = track.country

        val cornerRadius = 8F
        Glide.with(this.applicationContext).load(track.getCoverArtwork()).fitCenter().dontAnimate()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(cornerRadius, applicationContext)))
            .into(binding.albumImage)

        play = binding.ivPlayButton

        preparePlayer()

        play.setOnClickListener {
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
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(progressTimer)
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        return format.format(Date(milliseconds))
    }

    private fun startProgressTimer() {
        mainThreadHandler?.post(progressTimer)
    }

    private fun createProgressTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                when (playerState) {
                    STATE_PLAYING -> {
                        playingProgress.text = SimpleDateFormat(
                            "mm:ss", Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                        mainThreadHandler?.postDelayed(this, PROGRESS_DELAY)
                    }

                    STATE_PAUSED -> {
                        mainThreadHandler?.removeCallbacks(this)
                    }

                    STATE_PREPARED, STATE_DEFAULT -> {
                        mainThreadHandler?.removeCallbacks(this)
                        playingProgress.text = getText(R.string.defaultProgressTime).toString()
                    }
                }
            }
        }
    }

}

