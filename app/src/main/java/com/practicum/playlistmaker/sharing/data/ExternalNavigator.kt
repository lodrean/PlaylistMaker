package com.practicum.playlistmaker.sharing.data

import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.EmailData

class ExternalNavigator() {
    private val intent: Intent = Intent()
    fun shareLink(appLink: String) {

        intent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, appLink)
        }
        /* Intent.createChooser(
             shareIntent,
             context.getString(R.string.app_link_share_title)
         )
     }*/
    }

    fun openLink(termsLink: String) {
        intent.apply {
            data = Uri.parse(termsLink)
        }

    }

    fun openEmail(emailData: EmailData) {
        intent.apply {
            data = Uri.parse(
                "mailto:" + Uri.encode(emailData.email) + "?subject=" + Uri.encode(
                    emailData.subject
                ) + "&body=" + Uri.encode(emailData.body)
            )
        }
        }

    }


