package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediatekaBinding

class MediatekaFragment : BindingFragment<FragmentMediatekaBinding>() {

    private lateinit var tabsMediator: TabLayoutMediator

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediatekaBinding {
        return FragmentMediatekaBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.viewPager.adapter = MediatekaPagerAdapter(
            childFragmentManager, lifecycle
        )

        tabsMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabsMediator.attach()
    }
}