package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTrackName: TextView = itemView.findViewById(R.id.tv_trackName)
        private val tvArtistName: TextView = itemView.findViewById(R.id.tv_artistName)
        private val tvTrackTime: TextView = itemView.findViewById(R.id.tv_trackTime)
        private val ivArtwork: ImageView = itemView.findViewById(R.id.iv_artwork)

        fun bind(model: Track) {
            tvTrackName.text = model.trackName
            tvArtistName.text = model.artistName
            tvTrackTime.text = model.trackTime
            Glide.with(this.itemView.context)
                .load(model.artworkUrl100)
                .fitCenter().dontAnimate()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(8F, itemView.context))).into(ivArtwork)
        }

        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
            ).toInt()
        }
    }

}