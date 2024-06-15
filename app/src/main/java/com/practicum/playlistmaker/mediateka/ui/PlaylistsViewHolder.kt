package com.practicum.playlistmaker.mediateka.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.search.ui.dpToPx

class PlaylistsViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val playlistCount: TextView = itemView.findViewById(R.id.playlistCount)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val ivPlaylistCover: ImageView = itemView.findViewById(R.id.ivPlaylistCover)

    fun bind(playlist: Playlist){
        playlistName.text = playlist.playlistName
        playlistCount.text = playlist.tracksCount.toString()
        Glide.with(this.itemView.context)
            .load(playlist.imageUri)
            .fitCenter()
            .dontAnimate()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(8F, itemView.context)))
            .into(ivPlaylistCover)
    }
}