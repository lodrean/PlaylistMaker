package com.practicum.playlistmaker
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener { super.finish() }

        var jsonTrack = "0"
        val extras: Bundle? = intent.extras
        if (extras!=null) {
            jsonTrack = extras.getString("track").toString()
        }
        val track: Track = Gson().fromJson(/* json = */ jsonTrack, /* classOfT = */ Track::class.java)
        val albumImage : ImageView = findViewById(R.id.album_image)
        val trackName: TextView = findViewById(R.id.tv_trackTitle)
        val artistName: TextView = findViewById(R.id.tv_trackArtist)
        val progressTime: TextView = findViewById(R.id.tv_playing_progress)
        val duration: TextView = findViewById(R.id.tv_duration_time)
        val albumName: TextView = findViewById(R.id.tv_album_name)
        val year: TextView = findViewById(R.id.tv_year_value)
        val genre: TextView = findViewById(R.id.tv_genre_value)
        val country: TextView = findViewById(R.id.tv_country_value)

        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = formatMilliseconds(track.trackTime.toLong())
        albumName.text = track.collectionName
        year.text =track.releaseDate.removeRange(4, track.releaseDate.lastIndex+1)
        genre.text = track.genre
        country.text = track.country

        Glide.with(this.applicationContext)
            .load(track.getCoverArtwork())
            .fitCenter().dontAnimate()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(8F, applicationContext))).into(albumImage)


    }
}
fun formatMilliseconds(milliseconds: Long): String {
    val format = SimpleDateFormat("mm:ss", Locale.getDefault())
    return format.format(Date(milliseconds))
}
