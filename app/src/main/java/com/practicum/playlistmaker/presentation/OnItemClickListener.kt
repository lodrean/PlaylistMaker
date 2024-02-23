package com.practicum.playlistmaker.presentation

import com.practicum.playlistmaker.domain.models.Track


fun interface OnItemClickListener {
    fun onItemClick(track: Track)
}