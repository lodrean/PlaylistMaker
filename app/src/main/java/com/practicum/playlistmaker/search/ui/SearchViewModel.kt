package com.practicum.playlistmaker.search.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    application: Application,
    private val tracksInteractor: TracksInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
) : AndroidViewModel(application) {


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }

    private val showToast = SingleLiveEvent<String>()
    private var latestSearchText: String? = null
    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData


    fun observeShowToast(): LiveData<String> = showToast

    fun addTrackToHistory(track: Track) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tracksHistoryInteractor.addTrackToHistory(track)
            }
        }
    }



    fun showHistoryTrackList() {
        val trackList = mutableListOf<Track>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tracksHistoryInteractor.getItems()
                    .collect { tracks ->
                        trackList.addAll(tracks)
                    }
            }
            renderState(SearchState.History(trackList))
        }
    }

    fun clearHistory() {
        tracksHistoryInteractor.clearHistory()
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {

            renderState(SearchState.Loading)
            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: ArrayList<Track>?, errorMessage: String?) {
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
    }

    private fun showToast(message: String) {
        showToast.postValue(message)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

}