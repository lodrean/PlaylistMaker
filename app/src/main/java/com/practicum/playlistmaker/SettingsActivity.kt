package com.practicum.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener { super.finish() }

        var themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitch)
        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
        if (isDarkModeOn) {
            themeSwitcher.setChecked(true)
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
        val supportButton = findViewById<FrameLayout>(R.id.supportButton)
        val selectorIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(
                "mailto:" + Uri.encode(getString(R.string.my_email)) + "?subject=" + Uri.encode(
                    getString(R.string.theme_support)
                ) + "&body=" + Uri.encode(getString(R.string.body_support))
            )
        }

        supportButton.setOnClickListener {
            startActivity(Intent.createChooser(selectorIntent, "Send email..."))
        }


        val shareButton = findViewById<FrameLayout>(R.id.shareButton)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.app_link))
        }
        shareButton.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    shareIntent,
                    getString(R.string.app_link_share_title)
                )
            )
        }


        val userAggreementButton = findViewById<FrameLayout>(R.id.userAggreement)

        val internetIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getString(R.string.user_aggreement_link))
        }
        userAggreementButton.setOnClickListener {

            startActivity(internetIntent)
        }
    }
}

