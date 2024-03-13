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
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayer
import com.practicum.playlistmaker.search.domain.Constant
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.util.Creator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SearchViewModel(private val context: Context) : ViewModel() {
    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var latestSearchText: String? = null

    private val tracksInteractor = Creator.provideTracksInteractor(context)
    private val handler = Handler(Looper.getMainLooper())

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
    private lateinit var searchHistory: TracksHistoryInteractor
    private val trackHistoryList: ArrayList<Track> = arrayListOf()
    private val trackList: ArrayList<Track> = arrayListOf()
    var inputText: String = AMOUNT_DEF
    private var isClickAllowed = true
    private var detailsRunnable: Runnable? = null
    private val searchRunnable = Runnable {
        searchRequest()
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


    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun launchAudioPlayer(track: Track) {
        val intent = Intent(this@SearchActivity, AudioPlayer::class.java)
        intent.putExtra(Constant.CHOSEN_TRACK, Json.encodeToString(track))
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


}