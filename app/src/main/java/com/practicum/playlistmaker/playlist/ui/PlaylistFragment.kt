package com.practicum.playlistmaker.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }


}