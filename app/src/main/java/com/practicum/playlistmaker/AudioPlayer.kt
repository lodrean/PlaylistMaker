package com.practicum.playlistmaker

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable

class AudioPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener { super.finish() }

        val track: Track = SearchHistory(applicationContext).getItems()[0]


    }
}