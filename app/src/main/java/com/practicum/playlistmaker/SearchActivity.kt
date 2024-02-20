package com.practicum.playlistmaker

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
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.data.network.ItunesApiService
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.AudioPlayer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private var binding: ActivitySearchBinding? = null
    private lateinit var placeholderMessage: TextView
    private lateinit var inputEditText: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    private lateinit var placeholder: View
    private lateinit var itunesService: ItunesApiService
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var itunesBaseUrl: String
    private lateinit var retrofit: Retrofit
    private lateinit var searchHistoryView: View
    private lateinit var searchView: View
    private lateinit var progressBar: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var clearHistory: Button
    private lateinit var searchHistory: SearchHistory
    private val trackHistoryList: ArrayList<Track> = arrayListOf()
    private val trackList: ArrayList<Track> = arrayListOf()
    var inputText: String = AMOUNT_DEF
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }

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
        searchHistory = SearchHistory(applicationContext)
        clearHistory = binding?.clearButton!!
        progressBar = binding?.progressBar!!
        itunesBaseUrl = ITUNES_BASE_URL
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
        retrofit = Retrofit.Builder().baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create()).build()

        recyclerView = binding?.searchRecyclerView!!
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.adapter = trackHistoryAdapter

        trackHistoryAdapter.updateItems(searchHistory.getItems())
        val onItemClickListener = OnItemClickListener { track ->
            if (clickDebounce()) {
                searchHistory.tracks
                searchHistory.addTrackToHistory(track)
                trackHistoryAdapter.updateItems(searchHistory.tracks!!)
                trackHistoryAdapter.run { notifyDataSetChanged() }
                launchAudioPlayer(track)
            }
        }
        searchHistoryView.visibility =
            if (trackHistoryAdapter.itemCount > 0) View.VISIBLE else View.GONE
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

        itunesService = retrofit.create(ItunesApiService::class.java)
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
            recyclerView.isVisible = false
            progressBar.isVisible = true
            itunesService.searchTracks(inputEditText.text.toString()).enqueue(/* callback = */
                object : Callback<TracksSearchResponse> {
                    override fun onResponse(
                        call: Call<TracksSearchResponse>, response: Response<TracksSearchResponse>
                    ) {
                        progressBar.isVisible = false
                        val tracklistResponse = response.body()?.results
                        if (response.isSuccessful) {
                            trackList.clear()
                            trackAdapter.notifyDataSetChanged()

                            if (tracklistResponse?.isNotEmpty() == true) {
                                placeholder.isVisible = false
                                recyclerView.isVisible = true
                                trackList.addAll(tracklistResponse)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (trackList.isEmpty()) {
                                placeholder.isVisible = true
                                showMessage(getString(R.string.nothing_found), "")
                                placeholderImage.setImageResource(R.drawable.placeholder_not_find)
                                refreshButton.isVisible = false
                            } else {
                                showMessage("", "")
                            }
                        } else {
                            showMessage(
                                getString(R.string.something_went_wrong),
                                response.code().toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
                        progressBar.isVisible = false
                        placeholder.isVisible = true
                        placeholderImage.setImageResource(R.drawable.placeholder_no_internet)
                        refreshButton.isVisible = true

                        showMessage(
                            getString(R.string.something_went_wrong), t.message.toString()
                        )
                        refreshButton.setOnClickListener {
                            itunesService.searchTracks(inputEditText.text.toString())
                                .enqueue(this)
                        }
                    }
                })
        }
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

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val AMOUNT_DEF = ""
        const val CHOSEN_TRACK = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
