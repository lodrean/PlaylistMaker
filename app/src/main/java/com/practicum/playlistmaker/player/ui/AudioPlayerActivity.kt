package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.search.domain.Constant
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AudioPlayer : AppCompatActivity() {
    private val viewModel by viewModel<AudioPlayerViewModel> {
        Log.d("activity", "extra = " + this.intent.extras?.getString(Constant.CHOSEN_TRACK))
        parametersOf(intent)
    }

    private var play: ImageView? = null
    private var playingProgress: TextView? = null
    private var binding: ActivityAudioPlayerBinding? = null
    private var mainThreadHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityAudioPlayerBinding.inflate(LayoutInflater.from(this))
        val view = binding?.root
        setContentView(view)


        val backButton = binding?.backButton
        playingProgress = binding?.tvPlayingProgress
        backButton?.setOnClickListener { super.finish() }
        /*viewModel = ViewModelProvider(
            this,
            AudioPlayerViewModel.getViewModelFactory(intent)
        )[AudioPlayerViewModel::class.java]*/

        mainThreadHandler = Handler(Looper.getMainLooper())
        viewModel.getPlayStatusLiveData().observe(this) {
            render(it)
        }
        viewModel.observeProgress().observe(this) {
            playingProgress?.text = it

        }


        play = binding?.ivPlayButton
        play?.setOnClickListener {
            viewModel.playControl()
        }
        viewModel.createAudioPlayer()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun showTrackInfo(track: Track) {
        val cornerRadius = 8F
        Log.d("myTag", "track" + track.toString())
        binding?.albumImage?.let {
            Glide.with(this).load(track.getCoverArtwork()).fitCenter()
                .dontAnimate().placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(cornerRadius, applicationContext))).into(it)
        }
        binding?.tvTrackTitle?.text = track.trackName
        binding?.tvTrackArtist?.text = track.artistName
        binding?.tvDurationTime?.text = track.trackTime.let { formatMilliseconds(it.toLong()) }
        binding?.tvAlbumName?.text = track.collectionName
        binding?.tvYearValue?.text =
            track.releaseDate.removeRange(4, track.releaseDate.lastIndex + 1)
        binding?.tvGenreValue?.text = track.genre
        binding?.tvCountryValue?.text = track.country
    }

    private fun render(state: PlaybackState) {
        when (state) {
            is PlaybackState.Prepared -> {
                play?.isEnabled = true
            }

            is PlaybackState.Play -> play?.setImageResource(R.drawable.play_button)
            is PlaybackState.Pause -> play?.setImageResource(R.drawable.pause_button)
            is PlaybackState.Content -> showTrackInfo(state.track)
        }
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        return format.format(Date(milliseconds))
    }
}

