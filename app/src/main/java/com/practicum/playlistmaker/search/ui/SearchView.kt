package com.practicum.playlistmaker.search.ui

interface SearchView {

    // Методы, меняющие внешний вид экрана

    fun render(state: SearchState)

    // Методы «одноразовых событий»

    fun showToast(additionalMessage: String)

}

