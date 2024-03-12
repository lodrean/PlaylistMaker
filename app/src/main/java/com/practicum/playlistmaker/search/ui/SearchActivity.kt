package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayer
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_TRACK
import com.practicum.playlistmaker.search.domain.OnItemClickListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class SearchActivity : AppCompatActivity() {

    private var binding: ActivitySearchBinding? = null
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
    private val tracksInteractor = Creator.provideTracksInteractor(this)
    private lateinit var searchHistory: TracksHistoryInteractor
    private val trackHistoryList: ArrayList<Track> = arrayListOf()
    private val trackList: ArrayList<Track> = arrayListOf()
    var inputText: String = AMOUNT_DEF
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var detailsRunnable: Runnable? = null
    private val searchRunnable = Runnable {
        searchRequest()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(LayoutInflater.from(this))
        val view = binding?.root
        setContentView(view)
        val clearButton = binding?.clearIcon
        val backButton = binding?.back

        inputEditText = binding?.inputEditText!!
        placeholder = binding?.placeholderView!!
        placeholderMessage = binding?.placeholderTV!!
        placeholderImage = binding?.placeholderIV!!
        refreshButton = binding?.refreshButton!!
        searchHistoryView = binding?.searchHistoryGroupView!!
        searchView = binding?.searchView!!
        searchHistoryRecyclerView = binding?.searchHistoryRecyclerView!!
        searchHistory = Creator.provideTracksHistoryInteractor(applicationContext, this.intent)
        clearHistory = binding?.clearButton!!
        progressBar = binding?.progressBar!!
        /*itunesBaseUrl = ITUNES_BASE_URL*/
        inputEditText.setText(inputText)
        backButton?.setOnClickListener {
            super.finish()
        }

        val onHistoryItemClickListener = OnItemClickListener { track ->
            if (clickDebounce()) {
                launchAudioPlayer(track)
            }
        }
        val trackHistoryAdapter = TrackHistoryAdapter(onHistoryItemClickListener)

        recyclerView = binding?.searchRecyclerView!!
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.adapter = trackHistoryAdapter

        trackHistoryAdapter.updateItems(searchHistory.getItems())
        val onItemClickListener = OnItemClickListener { track ->
            if (clickDebounce()) {
                searchHistory.getItems()
                searchHistory.addTrackToHistory(track)
                trackHistoryAdapter.updateItems(searchHistory.getItems())
                trackHistoryAdapter.run { notifyDataSetChanged() }
                launchAudioPlayer(track)
            }
        }
        searchHistoryView.isVisible =
            trackHistoryAdapter.itemCount > 0
        trackAdapter = TrackAdapter(trackList, onItemClickListener)

        clearButton?.setOnClickListener {
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            inputEditText.setText("")
            placeholder.isVisible = false
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }
        clearHistory.setOnClickListener {
            searchHistory.clearHistory()
            trackHistoryAdapter.updateItems(trackHistoryList)
            searchHistoryView.isVisible = false
            trackHistoryAdapter.run { notifyDataSetChanged() }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton?.visibility = clearButtonVisibility(s)
                searchView.isVisible =
                    !(inputEditText.hasFocus() && s?.isEmpty() == true)
                if (s?.isEmpty() == true) trackList.clear()
                searchHistoryView.isVisible =
                    inputEditText.hasFocus() == true && s?.isEmpty() == true && (trackHistoryAdapter.itemCount > 0)
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                inputText.plus(s)
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
        recyclerView.adapter = trackAdapter
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest()
            }
            false
        }
    }

    private fun searchRequest() {
        if (inputEditText.text?.isNotEmpty() == true) {
            inProgressSearch()
            tracksInteractor.searchTracks(
                expression = inputEditText.text.toString(),
                consumer = object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?) {
                        val currentRunnable = detailsRunnable
                        if (currentRunnable != null) {
                            handler.removeCallbacks(currentRunnable)
                        }
                        val newDetailsRunnable = Runnable {
                            showSearchResults()
                            trackList.clear()
                            if (foundTracks != null) {
                                trackList.addAll(foundTracks)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (errorMessage != null) {
                                showNoConnectionMessage(errorMessage)
                            } else if (trackList.isEmpty()) {
                                showEmptyResults()
                            }
                        }
                        detailsRunnable = newDetailsRunnable
                        handler.post(newDetailsRunnable)
                    }

                }
            )
        }
    }

    override fun onDestroy() {
        val currentRunnable = detailsRunnable
        if (currentRunnable != null) {
            handler.removeCallbacks(currentRunnable)
        }

        super.onDestroy()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun launchAudioPlayer(track: Track) {
        val intent = Intent(this@SearchActivity, AudioPlayer::class.java)
        intent.putExtra(CHOSEN_TRACK, Json.encodeToString(track))
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_AMOUNT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(TEXT_AMOUNT, AMOUNT_DEF)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.visibility = View.VISIBLE
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
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

    private fun inProgressSearch() {
        placeholder.isVisible = false
        recyclerView.isVisible = false
        progressBar.isVisible = true
    }

    private fun showSearchResults() {
        placeholder.isVisible = false
        progressBar.isVisible = false
        recyclerView.isVisible = true
    }

    private fun showEmptyResults() {
        placeholder.isVisible = true
        showMessage(getString(R.string.nothing_found), "")
        placeholderImage.setImageResource(R.drawable.placeholder_not_find)
        refreshButton.isVisible = false
    }

    private fun showNoConnectionMessage(errorMessage: String) {
        placeholder.isVisible = true
        showMessage(getString(R.string.something_went_wrong), errorMessage)
        placeholderImage.setImageResource(R.drawable.placeholder_no_internet)
        refreshButton.isVisible = false
    }

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
