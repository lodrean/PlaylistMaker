package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_PLAYLIST
import com.practicum.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {
    private lateinit var onPlaylistsClickListener : (Playlist) -> Unit
    private val viewModel: PlaylistsViewModel by viewModel ()
    private var adapter: PlaylistsAdapter? = null
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
        onPlaylistsClickListener = debounce(
            delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false
        ) { playlist ->
            findNavController().navigate(R.id.action_mediatekaFragment_to_playlistFragment, bundleOf(CHOSEN_PLAYLIST to playlist.playlistId))
        }


        adapter = PlaylistsAdapter(requireContext(), onPlaylistsClickListener)


        binding.recyclerView.layoutManager = GridLayoutManager(
            requireActivity(), /*Количество столбцов*/
            2
        ) //ориентация по умолчанию — вертикальная
        binding.recyclerView.adapter = adapter
        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PlaylistsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty()
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.recyclerView.isVisible = true
        binding.placeholderIV.isVisible = false
        binding.placeholderTV.isVisible = false

        adapter?.clear()
        adapter?.setPlaylists(playlists)
        adapter?.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.recyclerView.isVisible = false
        binding.placeholderIV.isVisible = true
        binding.placeholderTV.isVisible = true
    }

}
