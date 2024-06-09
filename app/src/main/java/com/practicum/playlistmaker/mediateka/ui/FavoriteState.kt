package com.practicum.playlistmaker.mediateka.ui

import com.practicum.playlistmaker.search.domain.Track


sealed interface FavoriteState {

    data class Content(val tracks: List<Track>) : FavoriteState

    data object Empty : FavoriteState
}