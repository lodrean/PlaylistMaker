package com.practicum.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.domain.models.Track
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvTrackName: TextView = itemView.findViewById(R.id.tv_trackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tv_artistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tv_trackTime)
    private val ivArtwork: ImageView = itemView.findViewById(R.id.iv_artwork)
    fun bind(model: Track) {
        tvTrackName.text = model.trackName
        tvArtistName.text = model.artistName
        tvTrackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime.toLong())
        Glide.with(this.itemView.context)
            .load(model.artworkUrl100)
            .fitCenter()
            .dontAnimate()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(8F, itemView.context)))
            .into(ivArtwork)
    }
}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
    ).toInt()
}