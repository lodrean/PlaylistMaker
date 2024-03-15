package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.Track

interface MoviesView {

    // Методы, меняющие внешний вид экрана

    fun render(state: SearchState)

    // Методы «одноразовых событий»

    fun showToast(additionalMessage: String)

}

