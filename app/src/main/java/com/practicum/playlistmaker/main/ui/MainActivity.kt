package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.ui.MediatekaActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    lateinit var mediaButton: Button
    lateinit var searchButton: Button
    lateinit var settingsButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchButton = findViewById<Button>(R.id.search_button)
        mediaButton = findViewById<Button>(R.id.mediateka_button)
        settingsButton = findViewById<Button>(R.id.settings_button)


        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        val mediaClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(Intent(this@MainActivity, MediatekaActivity::class.java))
            }
        }
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        mediaButton.setOnClickListener(mediaClickListener)
    }
}