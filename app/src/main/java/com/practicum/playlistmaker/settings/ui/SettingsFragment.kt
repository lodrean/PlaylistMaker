package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {


    private val viewModel by viewModel<SettingsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themeSwitcher = binding.themeSwitch

        if (viewModel.getThemeSettings()) {
            themeSwitcher.isChecked = true
        }
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->

            viewModel.updateThemeSettings(checked)
        }
        val supportButton = binding.supportButton

        supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        val shareButton = binding.shareButton

        shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        val userAgreementButton = binding.userAggreement

        userAgreementButton.setOnClickListener {
            viewModel.openTerms()
        }
        viewModel.observeState().observe(viewLifecycleOwner) {
        }
    }

}

