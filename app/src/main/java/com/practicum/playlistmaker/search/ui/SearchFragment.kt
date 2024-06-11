package com.practicum.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayer
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_TRACK
import com.practicum.playlistmaker.search.domain.OnItemClickListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.util.debounce
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class SearchFragment : BindingFragment<FragmentSearchBinding>() {


    private lateinit var placeholderMessage: TextView
    private lateinit var inputEditText: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    private lateinit var placeholder: View
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryView: View
    private lateinit var searchView: View
    private lateinit var progressBar: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var clearHistory: Button
    private lateinit var trackHistoryAdapter: TrackHistoryAdapter
    var inputText: String = AMOUNT_DEF
    private val handler = Handler(Looper.getMainLooper())
    private var detailsRunnable: Runnable? = null
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var onTrackHistoryClickDebounce: (Track) -> Unit

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
        val clearButton = binding.clearIcon
        inputEditText = binding.inputEditText
        placeholder = binding.placeholderView
        placeholderMessage = binding.placeholderTV
        placeholderImage = binding.placeholderIV
        refreshButton = binding.refreshButton
        searchHistoryView = binding.searchHistoryGroupView
        searchView = binding.searchView
        searchHistoryRecyclerView = binding.searchHistoryRecyclerView
        clearHistory = binding.clearButton
        progressBar = binding.progressBar
        inputEditText.setText(inputText)

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.addTrackToHistory(track)
            trackHistoryAdapter.run { notifyDataSetChanged() }
            launchAudioPlayer(track)
        }

        onTrackHistoryClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            launchAudioPlayer(track)
        }

        val onHistoryItemClickListener = OnItemClickListener { track ->
            onTrackHistoryClickDebounce(track)
        }
        trackHistoryAdapter = TrackHistoryAdapter(onHistoryItemClickListener)

        recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchHistoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        searchHistoryRecyclerView.adapter = trackHistoryAdapter

        val onItemClickListener = OnItemClickListener { track ->
            onTrackClickDebounce(track)
        }
        searchHistoryView.isVisible =
            trackHistoryAdapter.itemCount > 0
        trackAdapter = TrackAdapter(onItemClickListener)

        clearButton.setOnClickListener {
            trackAdapter.tracks.clear()
            inputEditText.setText("")
            trackHistoryAdapter.notifyDataSetChanged()
            viewModel.showHistoryTrackList()
            placeholder.isVisible = false
            val inputMethodManager =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }
        clearHistory.setOnClickListener {
            viewModel.clearHistory()
            searchHistoryView.isVisible = false
            trackHistoryAdapter.run { notifyDataSetChanged() }
        }
        binding.refreshButton.setOnClickListener {
            viewModel.searchRequest(inputEditText.text.toString())
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                inputEditText.requestFocus()

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                inputEditText.requestFocus()
                clearButton.visibility = clearButtonVisibility(s)
                viewModel.searchDebounce(changedText = s.toString())
                searchView.isVisible = s?.isEmpty() != true
                recyclerView.isVisible = s?.isNotEmpty() == true
                if (inputEditText.hasFocus() && s?.isEmpty() == true && (trackHistoryAdapter.itemCount > 0)) {
                    viewModel.showHistoryTrackList()
                }
                if (s?.isEmpty() == true) trackAdapter.tracks.clear()
            }

            override fun afterTextChanged(s: Editable?) {
                inputText.plus(s)

            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
        recyclerView.adapter = trackAdapter


        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        /*viewModel.observeTrackList().observe(viewLifecycleOwner) {
            inputEditText.requestFocus()
            if (it.isNotEmpty()) showContent(it)
            Log.d("tracadapter61", "trackadapter - ${trackAdapter.tracks.size}")
        }*/

        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchRequest(inputEditText.text.toString())
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        inputEditText.requestFocus()
        if (trackAdapter.tracks.isNotEmpty() && inputEditText.text.isNotEmpty()) {
            val request = inputEditText.text.toString()
            showSearchView()
            viewModel.searchRequest(request)
            trackAdapter.notifyDataSetChanged()
        } else {
            searchView.isVisible = false
            viewModel.showHistoryTrackList()
            trackHistoryAdapter.notifyDataSetChanged()
        }


    }

    override fun onStart() {
        super.onStart()
        inputEditText.requestFocus()
        trackAdapter.notifyDataSetChanged()
        if (trackAdapter.tracks.isNotEmpty() && inputEditText.text.isNotEmpty()) {
            showSearchView()
        } else {
            viewModel.showHistoryTrackList()
        }
    }

    private fun showSearchView() {
        searchView.isVisible = true
        placeholder.isVisible = false
        searchHistoryView.isVisible = false
        progressBar.isVisible = false
        recyclerView.isVisible = true
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
            placeholderMessage.visibility = View.VISIBLE
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(
                    requireContext().applicationContext,
                    additionalMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }


    private fun showLoading() {
        placeholder.isVisible = false
        recyclerView.isVisible = false
        progressBar.isVisible = true

    }

    private fun showError(errorMessage: String) {
        searchView.isVisible = true
        recyclerView.isVisible = false
        searchHistoryView.isVisible = false
        placeholder.isVisible = true
        showMessage(getString(R.string.something_went_wrong), errorMessage)
        placeholderImage.setImageResource(R.drawable.placeholder_no_internet)
        refreshButton.isVisible = true
        progressBar.isVisible = false
    }

    private fun showEmpty(emptyMessage: String) {
        searchView.isVisible = true
        recyclerView.isVisible = false
        searchHistoryView.isVisible = false
        placeholder.isVisible = true
        showMessage(emptyMessage, "")
        placeholderImage.setImageResource(R.drawable.placeholder_not_find)
        refreshButton.isVisible = false
        progressBar.isVisible = false
    }

    private fun showContent(trackList: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(trackList)
        if (trackList.isNotEmpty()) {
            showSearchView()
        } else {
            searchView.isVisible = false
        }
        trackAdapter.notifyDataSetChanged()
    }

    private fun showHistoryContent(trackHistoryList: List<Track>) {
        if (trackHistoryList.isEmpty()) {
            searchHistoryView.isVisible = false
        } else {
            searchView.isVisible = false
            trackHistoryAdapter.tracks.clear()
            trackHistoryAdapter.tracks.addAll(trackHistoryList)
            trackHistoryAdapter.notifyDataSetChanged()
            placeholder.isVisible = false
            searchHistoryView.isVisible = true
            progressBar.isVisible = false
            recyclerView.isVisible = false
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> {
                showContent(state.trackList)
            }

            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Loading -> showLoading()
            is SearchState.History -> showHistoryContent(state.trackHistoryList)
        }
    }

    companion object {
        const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}
