package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Content(
        val movies: List<Track>
    ) : SearchState

    data class Error(
        val errorMessage: String
    ) : SearchState

    data class Empty(
        val message: String
    ) : SearchState

}