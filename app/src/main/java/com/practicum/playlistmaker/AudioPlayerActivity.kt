package com.practicum.playlistmaker
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.SearchActivity.Companion.CHOSEN_TRACK
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener { super.finish() }
        var track = Track(0, "", "", "", "", "", "", "", "")
        val extras: Bundle? = intent.extras
        extras?.let {
            val jsonTrack: String? = it.getString(CHOSEN_TRACK)
            track = Json.decodeFromString(jsonTrack!!)
        }

        binding.tvTrackTitle.text = track.trackName
        binding.tvTrackArtist.text = track.artistName
        binding.tvDurationTime.text = formatMilliseconds(track.trackTime.toLong())
        binding.tvAlbumName.text = track.collectionName
        binding.tvYearValue.text =
            track.releaseDate.removeRange(4, track.releaseDate.lastIndex + 1)
        binding.tvGenreValue.text = track.genre
        binding.tvCountryValue.text = track.country

        val cornerRadius = 8F
        Glide.with(this.applicationContext)
            .load(track.getCoverArtwork())
            .fitCenter().dontAnimate()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(cornerRadius, applicationContext)))
            .into(binding.albumImage)
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        return format.format(Date(milliseconds))
    }
}

