package com.practicum.playlistmaker.playlist.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import com.practicum.playlistmaker.search.ui.dpToPx
import com.practicum.playlistmaker.util.debounce
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val cornerRadius = 8F

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {


    override fun createBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var bottomSheetAttributesBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var trackAdapter: PlaylistBottomAdapter
    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        var playlistName = ""
        viewModel.getPlaylistLiveData().observe(viewLifecycleOwner) {
            render(it)
            when (it) {
                is PlaylistState.Content -> {
                    playlistName = it.playlist.playlistName
                }
            }
        }
        val playlistID = arguments?.getString(CHOSEN_PLAYLIST)
        viewModel.fillData(playlistID)
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
        val onLongClickListener = OnLongClickListener { track ->
            confirmDialog = MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog
            )
                .setTitle("Хотите удалить трек?")
                .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                    // ничего не делаем
                }.setPositiveButton("Да") { dialog, which ->
                    viewModel.deleteTrack(track.trackId)
                    viewModel.updateFillData()
                }
            confirmDialog.show()

        }
        trackAdapter = PlaylistBottomAdapter(onItemClickListener, onLongClickListener)
        binding.bottomSheetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomSheetRecyclerView.adapter = trackAdapter


        val bottomSheetAttributesContainer = binding.attributesBottomSheet
        bottomSheetAttributesBehavior =
            BottomSheetBehavior.from(bottomSheetAttributesContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        bottomSheetAttributesBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.bottomSheetRecyclerView.isVisible = false
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        binding.bottomSheetRecyclerView.isVisible = true
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.bottomSheetRecyclerView.isVisible = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        binding.shareButton.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.attributesButton.setOnClickListener {
            bottomSheetAttributesBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.share.setOnClickListener {
            viewModel.sharePlaylist()
        }
        binding.delete.setOnClickListener {
            val dialogDeletePlaylist = MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog
            )
                .setTitle("Хотите удалить плейлист $playlistName?")
                .setNeutralButton(getString(R.string.cancel)) { dialog, which ->

                }.setPositiveButton("Да") { dialog, which ->
                    viewModel.deletePlaylist()
                    parentFragmentManager.popBackStack()
                }
            dialogDeletePlaylist.show()
        }
        binding.redact.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_redactPlaylistFragment, bundleOf(
                    CHOSEN_PLAYLIST to playlistID
                )
            )
        }
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
        when {
            trackList.isEmpty() -> {
                binding.outOfTracks.isVisible = true
            }

            else -> {
                binding.outOfTracks.isVisible = false
            }
        }
        binding.playlistImage.let {
            Glide.with(requireActivity())
                .load(playlist.imageUri)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
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
        binding.bottomSheetCover.let {
            Glide.with(requireActivity())
                .load(playlist.imageUri)
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        dpToPx(
                            cornerRadius,
                            requireContext()
                        )
                    )
                )
                .into(it)
        }
        binding.bottomSheetPlaylistName.text = playlist.playlistName
        binding.bottomSheetTracksCount.text = context?.resources?.getQuantityString(
            R.plurals.numberOfTracks,
            playlist.tracksCount,
            playlist.tracksCount
        )
        trackAdapter.notifyDataSetChanged()

    }

    private fun launchAudioPlayer(track: Track) {
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(CHOSEN_TRACK, Json.encodeToString(track))
        startActivity(intent)
    }
}

