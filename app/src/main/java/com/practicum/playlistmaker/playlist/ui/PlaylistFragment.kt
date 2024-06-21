package com.practicum.playlistmaker.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_PLAYLIST
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {


    override fun createBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

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

    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showPlayListInfo(state.playlist, state.duration, state.trackList.size.toString())
        }
    }

    private fun showPlayListInfo(playlist: Playlist, duration: String, tracksCount: String) {

        binding.playlistImage.let {
            Glide.with(requireActivity())
                .load(playlist.imageUri)
                .into(it)
        }
        binding.tvPlaylistTitle.text = playlist.playlistName
        binding.tvPlaylistDuration.text = duration
        binding.tvPlaylistDescription.text = playlist.description
        binding.tvTracksCount.text = context?.resources?.getQuantityString(R.plurals.numberOfTracks, playlist.tracksCount, playlist.tracksCount)

    }
}

