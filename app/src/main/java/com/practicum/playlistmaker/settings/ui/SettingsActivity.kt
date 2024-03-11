package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.practicum.playlistmaker.settings.data.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener { super.finish() }

        val themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitch)
        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
        if (isDarkModeOn) {
            themeSwitcher.isChecked = true
        }

        val sharingInteractor = Creator.provideSharingInteractor(this)

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
        val supportButton = findViewById<FrameLayout>(R.id.supportButton)
        val selectorIntent = sharingInteractor.openSupport()

        supportButton.setOnClickListener {
            startActivity(selectorIntent as Intent)
        }


        val shareButton = findViewById<FrameLayout>(R.id.shareButton)
        val shareIntent = sharingInteractor.shareApp()

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

