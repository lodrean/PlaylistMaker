package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.OnItemClickListener
import com.practicum.playlistmaker.search.domain.Track


class TrackHistoryAdapter(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {
    var tracks: MutableList<Track> = mutableListOf()
    private val limit = 10


    fun updateItems(newItems: MutableList<Track>) {
        val oldItems = tracks
        tracks = newItems

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldItems.size
            }

            override fun getNewListSize(): Int {
                return newItems.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].trackId == newItems[newItemPosition].trackId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }
        })
        diffResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {/*return tracks!!.size*/
        return if (tracks.size > limit) {
            limit
        } else {
            tracks.size
        }
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(tracks[holder.adapterPosition]) }
    }
}
