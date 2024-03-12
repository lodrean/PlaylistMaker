package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.settings.data.App

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener { super.finish() }

        val themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitch)

        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
        val sharingInteractor = Creator.provideSharingInteractor(this)
        if (isDarkModeOn) {
            themeSwitcher.isChecked = true
        }
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->

            (applicationContext as App).switchTheme(checked)
        }
        val supportButton = findViewById<FrameLayout>(R.id.supportButton)
        val selectorIntent: Intent = sharingInteractor.openSupport()

        supportButton.setOnClickListener {
            startActivity(selectorIntent)
        }


        val shareButton = findViewById<FrameLayout>(R.id.shareButton)
        val shareIntent: Intent = sharingInteractor.shareApp()

        shareButton.setOnClickListener {
            startActivity(shareIntent)
        }


        val userAggreementButton = findViewById<FrameLayout>(R.id.userAggreement)

        val internetIntent: Intent = sharingInteractor.openTerms()

        userAggreementButton.setOnClickListener {
            startActivity(internetIntent)
        }
    }


}

