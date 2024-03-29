package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayer
import com.practicum.playlistmaker.search.domain.Constant.Companion.CHOSEN_TRACK
import com.practicum.playlistmaker.search.domain.OnItemClickListener
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


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
    private lateinit var trackHistoryAdapter: TrackHistoryAdapter
    private val trackHistoryList: ArrayList<Track> = arrayListOf()
    var inputText: String = AMOUNT_DEF
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var detailsRunnable: Runnable? = null

    /*lateinit var viewModel: SearchViewModel*/
    private val viewModel by viewModel<SearchViewModel>() {
        parametersOf(intent)
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
        clearHistory = binding?.clearButton!!
        progressBar = binding?.progressBar!!
        inputEditText.setText(inputText)
        backButton?.setOnClickListener {
            super.finish()
        }

        /*viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]*/

        val onHistoryItemClickListener = OnItemClickListener { track ->
            if (clickDebounce()) {
                launchAudioPlayer(track)
            }
        }
        trackHistoryAdapter = TrackHistoryAdapter(onHistoryItemClickListener)

        recyclerView = binding?.searchRecyclerView!!
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.adapter = trackHistoryAdapter

        val onItemClickListener = OnItemClickListener { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                trackHistoryAdapter.updateItems(viewModel.getHistoyItems())
                trackHistoryAdapter.run { notifyDataSetChanged() }
                launchAudioPlayer(track)
            }
        }
        searchHistoryView.isVisible =
            trackHistoryAdapter.itemCount > 0
        trackAdapter = TrackAdapter(onItemClickListener)

        clearButton?.setOnClickListener {
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            inputEditText.setText("")
            placeholder.isVisible = false
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }
        clearHistory.setOnClickListener {
            viewModel.clearHistory()
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
                if (s?.isEmpty() == true) trackAdapter.tracks.clear()
                searchHistoryView.isVisible =
                    inputEditText.hasFocus() == true && s?.isEmpty() == true && (trackHistoryAdapter.itemCount > 0)
                viewModel.searchDebounce(changedText = s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                inputText.plus(s)
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
        recyclerView.adapter = trackAdapter


        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeShowToast().observe(this) { toast ->
            showToast(toast)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchRequest(inputEditText.text.toString())
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
        val intent = Intent(this@SearchActivity, AudioPlayer::class.java)
        intent.putExtra(CHOSEN_TRACK, Json.encodeToString(track))
        Log.d("test", "intent: " + Json.encodeToString(track))
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

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            trackAdapter.tracks.clear()
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

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private fun showLoading() {
        placeholder.isVisible = false
        recyclerView.isVisible = false
        progressBar.isVisible = true

    }

    private fun showError(errorMessage: String) {
        placeholder.isVisible = true
        showMessage(getString(R.string.something_went_wrong), errorMessage)
        placeholderImage.setImageResource(R.drawable.placeholder_no_internet)
        refreshButton.isVisible = false
        progressBar.isVisible = false
    }

    private fun showEmpty(emptyMessage: String) {
        placeholder.isVisible = true
        showMessage(emptyMessage, "")
        placeholderImage.setImageResource(R.drawable.placeholder_not_find)
        refreshButton.isVisible = false
        progressBar.isVisible = false
    }

    private fun showContent(trackList: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(trackList)
        trackAdapter.notifyDataSetChanged()
        placeholder.isVisible = false
        searchHistoryView.isVisible = false
        progressBar.isVisible = false
        recyclerView.isVisible = true

    }

    private fun showHistoryContent(trackHistoryList: List<Track>) {
        if (trackHistoryList.isNotEmpty()) {
            trackHistoryAdapter.tracks.addAll(trackHistoryList)
            trackAdapter.notifyDataSetChanged()
            placeholder.isVisible = false
            searchHistoryView.isVisible = true
            progressBar.isVisible = false
            recyclerView.isVisible = false
        } else {
            searchHistoryView.isVisible = false
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
