package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.util.BindingFragment

class FavoriteTracksFragment : BindingFragment<FragmentFavoriteTracksBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholderIV.setImageResource(R.drawable.placeholder_not_find)
        binding.placeholderTV.text = getText(R.string.empty_mediateka)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavoriteTracksFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}