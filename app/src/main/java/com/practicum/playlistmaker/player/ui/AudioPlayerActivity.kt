package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.dpToPx
import com.practicum.playlistmaker.util.Creator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AudioPlayer : AppCompatActivity() {


    private var track: Track? = Track()
    private var play: ImageView? = null
    private var playingProgress: TextView? = null
    private var binding: ActivityAudioPlayerBinding? = null
    private var mainThreadHandler: Handler? = null
    lateinit var viewModel: ViewModel
    private var mediaPlayer = Creator.provideAudioPlayerInteractor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(LayoutInflater.from(this))
        val view = binding?.root
        setContentView(view)
        val backButton = binding?.backButton
        playingProgress = binding?.tvPlayingProgress
        backButton?.setOnClickListener { super.finish() }
        viewModel = ViewModelProvider(
            this,
            AudioPlayerViewModel.getViewModelFactory()
        )[AudioPlayerViewModel::class.java]
        track = Creator.provideTracksHistoryInteractor(this.intent).getTrack()


        mainThreadHandler = Handler(Looper.getMainLooper())


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
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.destroy()
        progressTimer?.let { mainThreadHandler?.removeCallbacks(it) }
    }

    private fun showTrackInfo(track: Track) {
        binding?.tvTrackTitle?.text = track?.trackName
        binding?.tvTrackArtist?.text = track?.artistName
        binding?.tvDurationTime?.text = track?.trackTime?.let { formatMilliseconds(it.toLong()) }
        binding?.tvAlbumName?.text = track?.collectionName
        binding?.tvYearValue?.text =
            track?.releaseDate?.removeRange(4, track!!.releaseDate.lastIndex + 1)
        binding?.tvGenreValue?.text = track?.genre
        binding?.tvCountryValue?.text = track?.country
    }

    private fun render(state: PlaybackState) {
        when (state) {
            is PlaybackState.Prepared -> play?.isEnabled = true
            is PlaybackState.Play -> play?.setImageResource(R.drawable.pause_button)
            is PlaybackState.Pause -> play?.setImageResource(R.drawable.play_button)
            is Content -> showTrackInfo(state.track)
        }
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        return format.format(Date(milliseconds))
    }
}

