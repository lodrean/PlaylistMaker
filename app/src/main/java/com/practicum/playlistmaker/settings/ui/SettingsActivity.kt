package com.practicum.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.util.App

class SettingsActivity : AppCompatActivity() {
    private lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener { super.finish() }

        val themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitch)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        if (viewModel.getThemeSettings()) {
            themeSwitcher.isChecked = true
        }
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->

            viewModel.updateThemeSettings(checked)
        }
        val supportButton = findViewById<FrameLayout>(R.id.supportButton)

        supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        val shareButton = findViewById<FrameLayout>(R.id.shareButton)

        shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        val userAgreementButton = findViewById<FrameLayout>(R.id.userAggreement)

        userAgreementButton.setOnClickListener {
            viewModel.openTerms()
        }
        viewModel.observeState().observe(this) {
        }

    }


}

