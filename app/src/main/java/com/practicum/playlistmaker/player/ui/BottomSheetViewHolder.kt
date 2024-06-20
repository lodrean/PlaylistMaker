package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.Playlist

class BottomSheetViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private val playlistCount: TextView = itemView.findViewById(R.id.tv_tracksCount)
    private val playlistName: TextView = itemView.findViewById(R.id.tv_playlistName)
    private val ivPlaylistCover: ImageView = itemView.findViewById(R.id.playlistCover)

    fun bind(playlist: Playlist) {
        playlistName.text = playlist.playlistName
        playlistCount.text = context.resources.getQuantityString(R.plurals.numberOfTracks, playlist.tracksCount, playlist.tracksCount)
        Glide.with(this.itemView.context)
            .load(playlist.imageUri)
            .dontAnimate()
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(
                    com.practicum.playlistmaker.search.ui.dpToPx(
                        8F,
                        this.itemView.context
                    )
                )
            )
            .into(ivPlaylistCover)
    }
}