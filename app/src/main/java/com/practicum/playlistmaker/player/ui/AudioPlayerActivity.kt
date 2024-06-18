package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.new_playlist.ui.NewPlaylistFragment
import com.practicum.playlistmaker.player.domain.OnPlaylistClickListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {
    private val viewModel by viewModel<AudioPlayerViewModel> {
        parametersOf(intent)
    }
    private var binding: ActivityAudioPlayerBinding? = null
    private var mainThreadHandler: Handler? = null
    private lateinit var adapter: BottomSheetAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        binding?.backButton?.setOnClickListener { super.finish() }

        mainThreadHandler = Handler(Looper.getMainLooper())
        viewModel.getPlayStatusLiveData().observe(this) {
            binding?.tvPlayingProgress?.text = it.progress
            render(it)
        }

        viewModel.getBottomSheetLiveData().observe(this) {
            renderBottomSheet(it)
            Log.d("playlists", "${binding?.bottomSheetRecyclerView?.adapter?.itemCount}")
        }

        viewModel.observeShowToast().observe(this) { toast ->
            showToast(toast)
        }

        //BottomSheet


        binding?.newPlaylist?.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction
                .add(
                    R.id.audioPlayerFragmentContainerView,
                    NewPlaylistFragment.newInstance(),
                    "NewPlaylistFragment"
                )
                .commit()
            viewModel.fillData()
        }

        val bottomSheetContainer = binding?.playlistsBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer!!).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        val onPlaylistClickListener = OnPlaylistClickListener { playlist ->
            viewModel.addToPlaylist(playlist)
        }
        adapter = BottomSheetAdapter(onPlaylistClickListener, this)
        binding?.bottomSheetRecyclerView?.layoutManager = LinearLayoutManager(this)
        binding?.bottomSheetRecyclerView?.adapter = adapter

        binding?.ivAddToPlaylistButton?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {

                    /*BottomSheetBehavior.STATE_EXPANDED -> {
                        // загружаем рекламный баннер
                        viewModel.onPause()
                    }*/

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // останавливаем трейлер
                        viewModel.onPause()
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding?.overlay?.visibility = View.GONE
                    }

                    else -> {
                        binding?.overlay?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding?.overlay?.alpha = slideOffset
            }
        })

        //Addfavorite
        binding?.ivAddFavoriteButton?.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding?.ivPlayButton?.setOnClickListener {
            viewModel.playControl()
        }
        viewModel.createAudioPlayer()
        viewModel.fillData()
        adapter.notifyDataSetChanged()
        Log.d("playlists", "${binding?.bottomSheetRecyclerView?.adapter?.itemCount}")
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }


    private fun showTrackInfo(track: Track) {
        val cornerRadius = 8F
        binding?.albumImage?.let {
            Glide.with(this).load(track.getCoverArtwork()).fitCenter()
                .dontAnimate().placeholder(com.practicum.playlistmaker.R.drawable.placeholder)
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
        setFavoriteImage(track)
    }

    private fun render(state: PlaybackState) {
        when (state) {
            is PlaybackState.Prepared -> {
                binding?.ivPlayButton?.isEnabled = true
                binding?.ivPlayButton?.setImageResource(com.practicum.playlistmaker.R.drawable.play_button)
            }

            is PlaybackState.Default -> binding?.ivPlayButton?.setImageResource(com.practicum.playlistmaker.R.drawable.play_button)
            is PlaybackState.Play -> binding?.ivPlayButton?.setImageResource(com.practicum.playlistmaker.R.drawable.pause_button)
            is PlaybackState.Pause -> binding?.ivPlayButton?.setImageResource(com.practicum.playlistmaker.R.drawable.play_button)
            is PlaybackState.Content -> showTrackInfo(state.track)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderBottomSheet(state: BottomSheetState) {
        when (state) {
            is BottomSheetState.Content -> {
                adapter.playlists.clear()
                adapter.playlists.addAll(state.playlists)
                adapter.notifyDataSetChanged()
                Log.d("playlists", "${binding?.bottomSheetRecyclerView?.adapter?.itemCount}")
                binding?.bottomSheetRecyclerView?.isVisible = true
                Log.d("visibility", "${binding?.bottomSheetRecyclerView?.isVisible}")
            }

            is BottomSheetState.Empty -> {}
        }
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        return format.format(Date(milliseconds))
    }

    private fun setFavoriteImage(track: Track) {
        if (track.isFavorite) {
            binding?.ivAddFavoriteButton?.setImageResource(com.practicum.playlistmaker.R.drawable.is_favorite)
        } else {
            binding?.ivAddFavoriteButton?.setImageResource(com.practicum.playlistmaker.R.drawable.heart_button)
        }
    }

}

