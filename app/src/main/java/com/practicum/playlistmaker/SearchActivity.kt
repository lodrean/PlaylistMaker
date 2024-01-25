package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.searchretrofit.ItunesApi
import com.practicum.playlistmaker.searchretrofit.TracksResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    private lateinit var placeholder: View
    private lateinit var inputEditText: EditText
    private lateinit var itunesService: ItunesApi
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var itunesBaseUrl: String
    private lateinit var retrofit: Retrofit
    private lateinit var searchHistoryView: View
    private lateinit var searchView: View
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var clearHistory: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var onHistoryItemClickListener: OnItemClickListener
    private lateinit var trackHistoryAdapter: TrackHistoryAdapter
    private val trackHistoryList: ArrayList<Track> = arrayListOf()
    private val trackList: ArrayList<Track> = arrayListOf()
    var inputText: String = AMOUNT_DEF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val backButton = findViewById<Button>(R.id.back)
        inputEditText = findViewById(R.id.inputEditText)
        placeholder = findViewById(R.id.placeholderView)
        placeholderMessage = findViewById(R.id.placeholderTV)
        placeholderImage = findViewById(R.id.placeholderIV)
        refreshButton = findViewById(R.id.refreshButton)
        searchHistoryView = findViewById(R.id.searchHistoryGroupView)
        searchView = findViewById(R.id.searchView)
        searchHistoryRecyclerView = findViewById(R.id.searchHistoryRecyclerView)
        searchHistory = SearchHistory(applicationContext)
        clearHistory = findViewById(R.id.clearButton)
        itunesBaseUrl = ITUNES_BASE_URL
        inputEditText.setText(inputText)
        backButton.setOnClickListener {
            finish()
        }

        onHistoryItemClickListener = OnItemClickListener { track ->
            launchAudioPlayer(track)
        }
        trackHistoryAdapter = TrackHistoryAdapter(onHistoryItemClickListener)
        retrofit = Retrofit.Builder().baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create()).build()

        val recyclerView = findViewById<RecyclerView>(R.id.searchRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.adapter = trackHistoryAdapter

        trackHistoryAdapter.updateItems(searchHistory.getItems())
        onItemClickListener = OnItemClickListener { track ->
            searchHistory.tracks
            searchHistory.addTrackToHistory(track)
            trackHistoryAdapter.updateItems(searchHistory.tracks!!)
            trackHistoryAdapter.run { notifyDataSetChanged() }
            launchAudioPlayer(track)
        }
        searchHistoryView.visibility =
            if (trackHistoryAdapter.itemCount > 0) View.VISIBLE else View.GONE
        trackAdapter = TrackAdapter(trackList, onItemClickListener)

        clearButton.setOnClickListener {
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            inputEditText.setText("")
            placeholder.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }
        clearHistory.setOnClickListener {
            searchHistory.clearHistory()
            trackHistoryAdapter.updateItems(trackHistoryList)
            searchHistoryView.visibility = View.GONE
            trackHistoryAdapter.run { notifyDataSetChanged() }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) View.GONE else View.VISIBLE
                if (s?.isEmpty() == true) trackList.clear()
                searchHistoryView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && (trackHistoryAdapter.itemCount > 0)) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                inputText.plus(s)
            }
        }

        itunesService = retrofit.create(ItunesApi::class.java)
        inputEditText.addTextChangedListener(simpleTextWatcher)
        recyclerView.adapter = trackAdapter
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    itunesService.search(inputEditText.text.toString()).enqueue(/* callback = */
                        object : Callback<TracksResponse> {
                            override fun onResponse(
                                call: Call<TracksResponse>, response: Response<TracksResponse>
                            ) {
                                val tracklistResponse = response.body()?.results
                                if (response.isSuccessful) {
                                    trackList.clear()
                                    trackAdapter.notifyDataSetChanged()

                                    if (tracklistResponse?.isNotEmpty() == true) {
                                        placeholder.visibility = View.GONE
                                        trackList.addAll(tracklistResponse)
                                        trackAdapter.notifyDataSetChanged()
                                    }
                                    if (trackList.isEmpty()) {
                                        placeholder.visibility = View.VISIBLE
                                        showMessage(getString(R.string.nothing_found), "")
                                        placeholderImage.setImageResource(R.drawable.placeholder_not_find)
                                        refreshButton.visibility = View.GONE
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

                            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                                placeholder.visibility = View.VISIBLE
                                placeholderImage.setImageResource(R.drawable.placeholder_no_internet)
                                refreshButton.visibility = View.VISIBLE

                                showMessage(
                                    getString(R.string.something_went_wrong), t.message.toString()
                                )
                                refreshButton.setOnClickListener {
                                    itunesService.search(inputEditText.text.toString())
                                        .enqueue(this)
                                }
                            }
                        })
                }
            }
            false
        }
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

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val AMOUNT_DEF = ""
        const val CHOSEN_TRACK = "track"
    }
}
