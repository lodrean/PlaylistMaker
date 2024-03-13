package com.practicum.playlistmaker.sharing.data

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.EmailData
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(
    val context: Context,
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        return externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        return externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return getString(context, R.string.app_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = getString(context, R.string.my_email),
            subject = getString(context, R.string.theme_support),
            body = getString(context, R.string.body_support)
        )
    }

    private fun getTermsLink(): String {
        return getString(context, R.string.user_aggreement_link)
    }
}