package com.practicum.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent

class SearchViewModel(
    application: Application,
    private val tracksInteractor: TracksInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
) : AndroidViewModel(application) {


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

    }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    init {
        if (tracksHistoryInteractor.getItems().isNotEmpty())
            showHistoryTrackList()
    }

    private var latestSearchText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    fun addTrackToHistory(track: Track) {
        tracksHistoryInteractor.addTrackToHistory(track)
    }

    fun getHistoyItems(): MutableList<Track> {
        return tracksHistoryInteractor.getItems()
    }

    fun showHistoryTrackList() {
        val trackList = tracksHistoryInteractor.getItems()
        renderState(SearchState.History(trackList))
    }

    fun clearHistory() {
        tracksHistoryInteractor.clearHistory()
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY

        handler.postAtTime(
            searchRunnable, SEARCH_REQUEST_TOKEN, postTime
        )
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {

            renderState(SearchState.Loading)
            tracksInteractor.searchTracks(expression = newSearchText, consumer =

            { foundTracks, errorMessage ->
                val trackList = mutableListOf<Track>()
                if (foundTracks != null) {
                    trackList.addAll(foundTracks)
                }

                when {
                    errorMessage != null -> {
                        renderState(
                            SearchState.Error(
                                errorMessage = getApplication<Application>().getString(R.string.something_went_wrong),
                            )
                        )
                        showToast(errorMessage)
                    }

                    trackList.isEmpty() -> {
                        renderState(
                            SearchState.Empty(
                                message = getApplication<Application>().getString(R.string.nothing_found),
                            )
                        )
                    }

                    else -> {
                        renderState(
                            SearchState.Content(
                                trackList
                            )
                        )
                    }
                }
            })
        }
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }


    private fun showToast(message: String) {
        showToast.postValue(message)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

}