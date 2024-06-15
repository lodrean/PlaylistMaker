package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.Playlist

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tvPlaylistName: TextView = itemView.findViewById(R.id.tv_playlistName)
    private val tvPlaylistCount: TextView = itemView.findViewById(R.id.tv_playlistCount)
    private val playlistCover: ImageView = itemView.findViewById(R.id.playlistCover)
    fun bind(model: Playlist) {
        tvPlaylistName.text = model.playlistName
        tvPlaylistCount.text = model.tracksCount.toString()
        Glide.with(this.itemView.context)
            .load(model.imageUri)
            .fitCenter()
            .dontAnimate()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(8F, itemView.context)))
            .into(playlistCover)
    }
}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
    ).toInt()
}