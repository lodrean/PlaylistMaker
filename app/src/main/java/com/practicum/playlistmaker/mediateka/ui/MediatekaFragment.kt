package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediatekaBinding
import com.practicum.playlistmaker.databinding.FragmentMediatekaBinding
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding

class MediatekaFragment : BindingFragment<FragmentMediatekaBinding>() {

    private lateinit var tabsMediator: TabLayoutMediator

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediatekaBinding {
        return FragmentMediatekaBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.back.setOnClickListener { super.finish() }

        binding.viewPager.adapter = MediatekaPagerAdapter(
            supportFragmentManager,
            lifecycle
        )

        tabsMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabsMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabsMediator.detach()
    }
}