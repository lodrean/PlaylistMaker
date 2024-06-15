package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.player.domain.OnPlaylistClickListener

class PlaylistAdapter(
    private val onPlaylistClickListener: OnPlaylistClickListener
) : RecyclerView.Adapter<PlaylistViewHolder>() {
    val playlists: ArrayList<Playlist> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottomsheet, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { onPlaylistClickListener.onItemClick(playlists[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}

