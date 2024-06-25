package com.practicum.playlistmaker.search.domain

class Constant {
    companion object {
        const val CHOSEN_TRACK = "CHOSEN_TRACK"
        const val CHOSEN_PLAYLIST = "CHOSEN_PLAYLIST"

        //result codes
        const val DEFAULT_RESULT = 0
        const val SUCCESS = 200
        const val BAD_REQUEST = 400
        const val NO_CONNECTION = -1
    }
}