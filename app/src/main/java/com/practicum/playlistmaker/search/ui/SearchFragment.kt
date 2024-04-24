package com.practicum.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayer
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_TRACK
import com.practicum.playlistmaker.search.domain.OnItemClickListener
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class SearchFragment : BindingFragment<FragmentSearchBinding>() {



    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackHistoryAdapter
    private val trackHistoryList: ArrayList<Track> = arrayListOf()
    var inputText: String = AMOUNT_DEF
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var detailsRunnable: Runnable? = null

    private val viewModel by viewModel<SearchViewModel> {
        parametersOf(requireActivity().intent)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputEditText.setText(inputText)


        val onHistoryItemClickListener = OnItemClickListener { track ->
            if (clickDebounce()) {
                launchAudioPlayer(track)
            }
        }
        trackHistoryAdapter = TrackHistoryAdapter(onHistoryItemClickListener)

        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchHistoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.searchHistoryRecyclerView.adapter = trackHistoryAdapter

        val onItemClickListener = OnItemClickListener { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                trackHistoryAdapter.updateItems(viewModel.getHistoyItems())
                trackHistoryAdapter.run { notifyDataSetChanged() }
                launchAudioPlayer(track)
            }
        }
        binding.searchHistoryGroupView.isVisible =
            trackHistoryAdapter.itemCount > 0
        trackAdapter = TrackAdapter(onItemClickListener)

        binding.clearIcon.setOnClickListener {
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            binding.inputEditText.setText("")
            binding.placeholderView.isVisible = false
            val inputMethodManager =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.clearIcon.windowToken, 0)
        }
        binding.clearButton.setOnClickListener {
            viewModel.clearHistory()
            trackHistoryAdapter.updateItems(trackHistoryList)
            binding.searchHistoryGroupView.isVisible = false
            trackHistoryAdapter.run { notifyDataSetChanged() }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.inputEditText.requestFocus()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
                binding.searchView.isVisible =
                    !(binding.inputEditText.hasFocus() && s?.isEmpty() == true)
                if (s?.isEmpty() == true) trackAdapter.tracks.clear()
                binding.searchHistoryGroupView.isVisible =
                    binding.inputEditText.hasFocus() == true && s?.isEmpty() == true && (trackHistoryAdapter.itemCount > 0)
                viewModel.searchDebounce(changedText = s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                inputText.plus(s)
            }
        }

        binding.inputEditText.addTextChangedListener(simpleTextWatcher)
        binding.searchRecyclerView.adapter = trackAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchRequest(binding.inputEditText.text.toString())
            }
            false
        }

        viewModel.showHistoryTrackList()
    }

    override fun onDestroy() {
        val currentRunnable = detailsRunnable
        if (currentRunnable != null) {
            handler.removeCallbacks(currentRunnable)
        }
        super.onDestroy()
    }

    private fun launchAudioPlayer(track: Track) {
        val intent = Intent(requireContext(), AudioPlayer::class.java)
        intent.putExtra(CHOSEN_TRACK, Json.encodeToString(track))
        Log.d("test", "intent: " + Json.encodeToString(track))
        startActivity(intent)
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            binding.placeholderTV.visibility = View.VISIBLE
            binding.placeholderTV.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(
                    requireContext().applicationContext,
                    additionalMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            binding.placeholderTV.visibility = View.GONE
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private fun showLoading() {
        binding.placeholderView.isVisible = false
        binding.searchRecyclerView.isVisible = false
        binding.progressBar.isVisible = true

    }

    private fun showError(errorMessage: String) {
        binding.placeholderView.isVisible = true
        showMessage(getString(R.string.something_went_wrong), errorMessage)
        binding.placeholderIV.setImageResource(R.drawable.placeholder_no_internet)
        binding.refreshButton.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun showEmpty(emptyMessage: String) {
        binding.placeholderView.isVisible = true
        showMessage(emptyMessage, "")
        binding.placeholderIV.setImageResource(R.drawable.placeholder_not_find)
        binding.refreshButton.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun showContent(trackList: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(trackList)
        trackAdapter.notifyDataSetChanged()
        binding.placeholderView.isVisible = false
        binding.searchHistoryGroupView.isVisible = false
        binding.progressBar.isVisible = false
        binding.searchRecyclerView.isVisible = true

    }

    private fun showHistoryContent(trackHistoryList: List<Track>) {
        if (trackHistoryList.isNotEmpty()) {
            trackHistoryAdapter.tracks.addAll(trackHistoryList)
            trackAdapter.notifyDataSetChanged()
            binding.placeholderView.isVisible = false
            binding.searchHistoryGroupView.isVisible = true
            binding.progressBar.isVisible = false
            binding.searchRecyclerView.isVisible = false
        } else {
            binding.searchHistoryGroupView.isVisible = false
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.trackList)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Loading -> showLoading()
            is SearchState.History -> showHistoryContent(state.trackHistoryList)
        }
    }

}
