package com.practicum.playlistmaker.playlist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.OnItemClickListener
import com.practicum.playlistmaker.search.domain.OnLongClickListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.TrackViewHolder


class PlaylistBottomAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val onLongClickListener: OnLongClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {
    val tracks: ArrayList<Track> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(tracks[holder.adapterPosition])
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener.onItemClick(tracks[holder.adapterPosition])
           return@setOnLongClickListener true
        }



    }

}
