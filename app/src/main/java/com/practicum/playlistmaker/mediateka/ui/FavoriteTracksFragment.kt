package com.practicum.playlistmaker.mediateka.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.player.ui.AudioPlayer
import com.practicum.playlistmaker.search.domain.Constant
import com.practicum.playlistmaker.search.domain.OnItemClickListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.SearchFragment
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.util.debounce
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : BindingFragment<FragmentFavoriteTracksBinding>() {

    private val viewModel: FavoriteTracksViewModel by viewModel()
    private var adapter: TrackAdapter? = null
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var favoriteList: RecyclerView
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            launchAudioPlayer(track)
        }
        val onItemClickListener = OnItemClickListener { track ->
            onTrackClickDebounce(track)
        }

        adapter = TrackAdapter(onItemClickListener)

        favoriteList = binding.favoriteList
        favoriteList.layoutManager = LinearLayoutManager(requireContext())
        favoriteList.adapter = adapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Content -> showContent(state.tracks)
            is FavoriteState.Empty -> showEmpty()
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.favoriteList.isVisible = true
        binding.placeholderIV.isVisible = false
        binding.placeholderTV.isVisible = false
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.favoriteList.isVisible = false
        binding.placeholderIV.isVisible = true
        binding.placeholderTV.isVisible = true
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
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

    private fun launchAudioPlayer(track: Track) {
        val intent = Intent(requireContext(), AudioPlayer::class.java)
        intent.putExtra(Constant.CHOSEN_TRACK, Json.encodeToString(track))
        startActivity(intent)
    }

}