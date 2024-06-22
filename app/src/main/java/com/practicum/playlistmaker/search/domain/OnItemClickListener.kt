package com.practicum.playlistmaker.search.domain



fun interface OnItemClickListener {
    fun onItemClick(track: Track)
}
fun interface OnLongClickListener {
    fun onItemClick(track: Track)
}
