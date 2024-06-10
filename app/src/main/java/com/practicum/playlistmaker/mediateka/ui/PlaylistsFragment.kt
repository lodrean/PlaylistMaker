package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsViewModel by viewModel()
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholderIV.setImageResource(R.drawable.placeholder_not_find)
        binding.placeholderTV.text = getText(R.string.empty_playlists)
        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediatekaFragment_to_newPlaylistFragment
            )
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            PlaylistsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
