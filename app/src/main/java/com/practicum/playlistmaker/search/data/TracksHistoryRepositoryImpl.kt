package com.practicum.playlistmaker.search.data

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.Constant
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryRepository
import kotlinx.serialization.json.Json

const val TRACK_LIST_KEY = "key_for_track_list"
const val SHARED_PREFERENCES = "playlist_search_preferences"

class TracksHistoryRepositoryImpl(
    private val intent: Intent,
    private val prefs: SharedPreferences,
    private val gson: Gson
) :
    TracksHistoryRepository {

    private var tracks = mutableListOf<Track>()


    override fun getItems(): MutableList<Track> {

        val itemsFromCache = getItemsFromCache()
        tracks = itemsFromCache
        return tracks
    }

    override fun clearHistory() {
        prefs.edit().clear().apply()
    }

    override fun addTrackToHistory(track: Track) {
        tracks = getItemsFromCache()
        Log.d("TrackID", "track = " + track.trackId)
        if (track.trackId in tracks.map { it.trackId }) {
            Log.d("TrackID", "tracktoremove = " + tracks.size)
            tracks.remove(track)
            Log.d("TrackID", "trackafterremove = " + tracks.size)
            tracks.add(0, track)
            saveTracklist(prefs, tracks)
            Log.d("TrackID", "trackafteradding = " + tracks.size)
        } else {
            Log.d("TrackID", "tracknotremove = " + tracks)
            tracks.add(0, track)
            saveTracklist(prefs, tracks)
        }
    }

    override fun getTrackFromIntent(): Track {

        val extras: Bundle? = intent.extras
        val jsonTrack: String? = extras?.getString(Constant.CHOSEN_TRACK)
        Log.d("Track", "track = " + jsonTrack)
        return Json.decodeFromString(jsonTrack!!)
    }

    private fun getItemsFromCache(): MutableList<Track> {
        val json: String? = prefs.getString(TRACK_LIST_KEY, null)
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
        return gson.fromJson<MutableList<Track>>(
            json, object : TypeToken<MutableList<Track>>() {}.type
        )
    }

    private fun createJsonFromTracksList(tracks: MutableList<Track>?): String {
        return gson.toJson(tracks)
    }

}




