package com.practicum.playlistmaker

import com.practicum.playlistmaker.data.dto.Track

fun interface OnItemClickListener {
    fun onItemClick(track: Track)
}