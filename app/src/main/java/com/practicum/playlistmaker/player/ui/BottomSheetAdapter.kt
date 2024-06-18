package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.player.domain.OnPlaylistClickListener

class BottomSheetAdapter(
    private val onPlaylistClickListener: OnPlaylistClickListener,
    private val context: Context
) : RecyclerView.Adapter<BottomSheetViewHolder>() {

    var playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottomsheet, parent, false)
        return BottomSheetViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { onPlaylistClickListener.onItemClick(playlists[holder.adapterPosition]) }
    }
}