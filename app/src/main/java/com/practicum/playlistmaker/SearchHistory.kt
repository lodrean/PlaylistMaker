package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val TRACK_LIST_KEY = "key_for_track_list"
const val SHARED_PREFERENCES = "playlist_search_preferences"

class SearchHistory(private val context: Context) {

    private val sharedPref = context.getSharedPreferences(
        SHARED_PREFERENCES,
        AppCompatActivity.MODE_PRIVATE
    )
    var tracks: MutableList<Track>? = null

    fun getItems(): MutableList<Track> {

        val itemsFromCache = getItemsFromCache()
        tracks = itemsFromCache
        return tracks ?: mutableListOf()
    }

    private fun getItemsFromCache(): MutableList<Track> {
        val json: String? = sharedPref.getString(TRACK_LIST_KEY, null)
        return if (json != null) {
            createTracksListFromJson(json)
        } else {
            return mutableListOf()
        }

    }

    fun clearHistory() {
        sharedPref.edit()
            .clear()
            .apply()
    }

    fun addTrackToHistory(track: Track) {
        tracks = getItemsFromCache()
        if (track.trackId in tracks!!.map { it.trackId }) {
            tracks!!.remove(track)
            tracks!!.add(0, track)
            saveTracklist(sharedPref, tracks)
        } else {
            tracks!!.add(0, track)
            saveTracklist(sharedPref, tracks)
        }

    }

    fun saveTracklist(sharedPrefs: SharedPreferences, tracks: MutableList<Track>?) {
        sharedPrefs.edit()
            .putString(TRACK_LIST_KEY, createJsonFromTracksList(tracks))
            .apply()
    }

    fun createTracksListFromJson(json: String): MutableList<Track> {
        return Gson().fromJson<MutableList<Track>>(
            json,
            object : TypeToken<MutableList<Track>>() {}.type
        )
    }

    fun createJsonFromTracksList(tracks: MutableList<Track>?): String {
        return Gson().toJson(tracks)
    }

}




