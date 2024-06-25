package com.practicum.playlistmaker.mediateka.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.player.domain.OnPlaylistClickListener

class PlaylistsAdapter(
    private val context: Context,
    private val onPlaylistClickListener: OnPlaylistClickListener
) : RecyclerView.Adapter<PlaylistsViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return PlaylistsViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }


    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { onPlaylistClickListener.onItemClick(playlists[holder.adapterPosition]) }
    }

    fun setPlaylists(list: List<Playlist>) {
        playlists.addAll(list)
    }

    fun clear() {
        playlists.clear()
    }

}