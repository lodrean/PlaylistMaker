package com.practicum.playlistmaker.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.api.TracksHistoryRepository
import com.practicum.playlistmaker.domain.models.Constant
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.serialization.json.Json

const val TRACK_LIST_KEY = "key_for_track_list"
const val SHARED_PREFERENCES = "playlist_search_preferences"

class TracksHistoryRepositoryImpl(context: Context, private val intent: Intent) :
    TracksHistoryRepository {

    private var tracks = mutableListOf<Track>()

    private val sharedPref = context.getSharedPreferences(
        SHARED_PREFERENCES,
        AppCompatActivity.MODE_PRIVATE
    )

    override fun getItems(): MutableList<Track> {

        val itemsFromCache = getItemsFromCache()
        tracks = itemsFromCache
        return tracks
    }

    override fun clearHistory() {
        sharedPref.edit().clear().apply()
    }

    override fun addTrackToHistory(track: Track) {
        tracks = getItemsFromCache()
        if (track.trackId in tracks.map { it.trackId }) {
            tracks.remove(track)
            tracks.add(0, track)
            saveTracklist(sharedPref, tracks)
        } else {
            tracks.add(0, track)
            saveTracklist(sharedPref, tracks)
        }
    }

    override fun getTrackFromIntent(): Track {

        val extras: Bundle? = intent.extras
        val jsonTrack: String? = extras?.getString(Constant.CHOSEN_TRACK)
        return Json.decodeFromString(jsonTrack!!)
    }

    private fun getItemsFromCache(): MutableList<Track> {
        val json: String? = sharedPref.getString(TRACK_LIST_KEY, null)
        return if (json != null) {
            createTracksListFromJson(json)
        } else {
            return mutableListOf()
        }
    }

    private fun saveTracklist(sharedPrefs: SharedPreferences, tracks: MutableList<Track>?) {
        sharedPrefs.edit()
            .putString(TRACK_LIST_KEY, createJsonFromTracksList(tracks))
            .apply()
    }

    private fun createTracksListFromJson(json: String): MutableList<Track> {
        return Gson().fromJson<MutableList<Track>>(
            json, object : TypeToken<MutableList<Track>>() {}.type
        )
    }

    private fun createJsonFromTracksList(tracks: MutableList<Track>?): String {
        return Gson().toJson(tracks)
    }

}




