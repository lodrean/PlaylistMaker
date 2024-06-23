package com.practicum.playlistmaker.playlist.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.new_playlist.ui.NewPlaylistFragment
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_PLAYLIST
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class RedactPlaylistFragment : NewPlaylistFragment() {
    override val viewModel by viewModel<RedactPlaylistViewModel> {
        parametersOf(arguments?.getString(CHOSEN_PLAYLIST))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createButton.text = getString(R.string.save)
        binding.title.text = getString(R.string.to_redact)
        binding.createButton.setOnClickListener {
            if (binding.textInputName.text?.isNotEmpty() == true) {
                viewModel.updatePlaylist(
                    binding.textInputName.text.toString(),
                    binding.textInputDescription.text.toString()
                )

            }
            viewModel.fillData()
            parentFragmentManager.popBackStack()
        }
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )

        viewModel.getPlaylistLiveData().observe(viewLifecycleOwner) {
            renderNewValue(it)
        }
        viewModel.fillData()
    }

    private fun renderNewValue(state: RedactPlaylistState) {
        when (state) {
            is RedactPlaylistState.Content -> {
                showNewInfo(state.playlist)
            }
        }
    }

    private fun showNewInfo(playlist: Playlist) {
        binding.textInputName.setText(playlist.playlistName)
        binding.textInputDescription.setText(playlist.description)
        Log.d("image", playlist.imageUri)
        viewModel.setImage(playlist.imageUri.toUri())
        if (playlist.imageUri != "") {
            binding.imageView.isVisible = true
            binding.placeHolder.isVisible = false
        }

    }
}