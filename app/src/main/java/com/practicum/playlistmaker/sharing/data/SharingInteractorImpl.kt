package com.practicum.playlistmaker.sharing.data

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.new_playlist.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.sharing.domain.EmailData
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import java.text.SimpleDateFormat
import java.util.Locale

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

    override fun sharePlaylist(playlist: Playlist, tracklist: List<Track>) {
        var sharingText = ""
        var rowCount = 1
        sharingText += "${playlist.playlistName}\n" + playlist.description + "\n" + context.resources?.getQuantityString(
            R.plurals.numberOfTracks,
            playlist.tracksCount,
            playlist.tracksCount
        ) + "\n"
        for (track in tracklist) {
            sharingText += "$rowCount.${track.artistName}-${track.trackName}(${formatTrackTime(track.trackTime)})\n"
            rowCount += 1
        }
        return externalNavigator.shareText(sharingText)
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

    private fun formatTrackTime(trackTime: Int): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(trackTime)
    }

    private fun getTermsLink(): String {
        return getString(context, R.string.user_aggreement_link)
    }
}