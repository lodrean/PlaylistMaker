package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track


fun interface OnItemClickListener {
    fun onItemClick(track: Track)
}