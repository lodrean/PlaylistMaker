package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AudioPlayer : AppCompatActivity() {
    private val viewModel by viewModel<AudioPlayerViewModel> {
        parametersOf(intent)
    }

    private var binding: ActivityAudioPlayerBinding? = null
    private var mainThreadHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(LayoutInflater.from(this))
        val view = binding?.root
        setContentView(view)

        binding?.backButton?.setOnClickListener { super.finish() }

        mainThreadHandler = Handler(Looper.getMainLooper())
        viewModel.getPlayStatusLiveData().observe(this) {
            binding?.tvPlayingProgress?.text = it.progress
            render(it)

        }
        /* viewModel.observeProgress().observe(this) {
             binding?.tvPlayingProgress?.text = it

         }*/


        binding?.ivPlayButton?.setOnClickListener {
            viewModel.playControl()
        }
        viewModel.createAudioPlayer()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }


    private fun showTrackInfo(track: Track) {
        val cornerRadius = 8F
        binding?.albumImage?.let {
            Glide.with(this).load(track.getCoverArtwork()).fitCenter()
                .dontAnimate().placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(cornerRadius, applicationContext))).into(it)
        }
        binding?.tvTrackTitle?.text = track.trackName
        binding?.tvTrackArtist?.text = track.artistName
        binding?.tvDurationTime?.text = formatMilliseconds(track.trackTime.toLong())
        binding?.tvAlbumName?.text = track.collectionName
        binding?.tvYearValue?.text =
            track.releaseDate.removeRange(4, track.releaseDate.lastIndex + 1)
        binding?.tvGenreValue?.text = track.genre
        binding?.tvCountryValue?.text = track.country
    }

    private fun render(state: PlaybackState) {
        when (state) {
            is PlaybackState.Prepared -> {
                binding?.ivPlayButton?.isEnabled = true
                binding?.ivPlayButton?.setImageResource(R.drawable.play_button)
            }
            is PlaybackState.Default -> binding?.ivPlayButton?.setImageResource(R.drawable.play_button)
            is PlaybackState.Play -> binding?.ivPlayButton?.setImageResource(R.drawable.pause_button)
            is PlaybackState.Pause -> binding?.ivPlayButton?.setImageResource(R.drawable.play_button)
            is PlaybackState.Content -> showTrackInfo(state.track)
        }
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        return format.format(Date(milliseconds))
    }
}

