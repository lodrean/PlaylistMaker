package com.practicum.playlistmaker.playlist.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_PLAYLIST
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_TRACK
import com.practicum.playlistmaker.search.domain.OnItemClickListener
import com.practicum.playlistmaker.search.domain.OnLongClickListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.util.debounce
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {


    override fun createBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var trackAdapter: PlaylistBottomAdapter
    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        viewModel.getPlaylistLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.fillData(arguments?.getString(CHOSEN_PLAYLIST))
        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            launchAudioPlayer(track)
        }

        val onItemClickListener = OnItemClickListener { track ->
            onTrackClickDebounce(track)
        }
        val onLongClickListener = OnLongClickListener {track ->
            confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("Хотите удалить трек?")
                .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                    // ничего не делаем
                }.setPositiveButton("Да") { dialog, which ->
                    viewModel.deleteTrack(track.trackId)
                    parentFragmentManager.popBackStack()
                }
                confirmDialog.show()

        }
        trackAdapter =  PlaylistBottomAdapter(onItemClickListener, onLongClickListener)
        binding.bottomSheetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomSheetRecyclerView.adapter = trackAdapter
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showPlayListInfo(
                state.playlist,
                state.duration,
                state.trackList
            )
        }
    }

    private fun showPlayListInfo(playlist: Playlist, duration: Int, trackList: List<Track>) {

        binding.playlistImage.let {
            Glide.with(requireActivity())
                .load(playlist.imageUri)
                .into(it)
        }
        binding.tvPlaylistTitle.text = playlist.playlistName
        binding.tvPlaylistDuration.text =
            context?.resources?.getQuantityString(R.plurals.countOfMinutes, duration, duration)
        binding.tvPlaylistDescription.text = playlist.description
        binding.tvTracksCount.text = context?.resources?.getQuantityString(
            R.plurals.numberOfTracks,
            playlist.tracksCount,
            playlist.tracksCount
        )
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(trackList)
        trackAdapter.notifyDataSetChanged()

    }

    private fun launchAudioPlayer(track: Track) {
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(CHOSEN_TRACK, Json.encodeToString(track))
        startActivity(intent)
    }
}

