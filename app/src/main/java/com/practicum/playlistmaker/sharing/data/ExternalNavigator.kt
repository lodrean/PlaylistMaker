package com.practicum.playlistmaker.sharing.data

import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.EmailData

class ExternalNavigator {
    fun shareLink(appLink: String): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, appLink)
        }
        return Intent.createChooser(
            shareIntent,
            R.string.app_link_share_title.toString()
        )

    }

    fun openLink(termsLink: String): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(termsLink)
        }
    }

    fun openEmail(emailData: EmailData): Intent {
        val selectorIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(
                "mailto:" + Uri.encode(emailData.email) + "?subject=" + Uri.encode(
                    emailData.subject
                ) + "&body=" + Uri.encode(emailData.body)
            )
        }
        return Intent.createChooser(selectorIntent, "Send email...")

    }

}
