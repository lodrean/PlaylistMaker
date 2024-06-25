package com.practicum.playlistmaker.sharing.data


import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.EmailData

class ExternalNavigator(private val context: Context) {


    fun shareLink(appLink: String) {
        val myIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, appLink)
        }
        context.startActivity(
            Intent.createChooser(
                myIntent,
                context.getString(R.string.app_link_share_title)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }


    fun openLink(termsLink: String) {
        val myIntent = Intent(Intent.ACTION_VIEW).apply {

            data = Uri.parse(termsLink)
        }
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(myIntent)
    }

    fun openEmail(emailData: EmailData) {
        val myIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(
                "mailto:" + Uri.encode(emailData.email) + "?subject=" + Uri.encode(
                    emailData.subject
                ) + "&body=" + Uri.encode(emailData.body)
            )
        }
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(myIntent)
    }

    fun shareText(text: String) {
        val myIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(
            Intent.createChooser(
                myIntent,
                context.getString(R.string.share_playlist)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }


}


