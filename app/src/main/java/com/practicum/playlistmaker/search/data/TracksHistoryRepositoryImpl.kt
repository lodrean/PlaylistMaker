package com.practicum.playlistmaker.search.data

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.Constant
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

const val TRACK_LIST_KEY = "key_for_track_list"
const val SHARED_PREFERENCES = "playlist_search_preferences"

class TracksHistoryRepositoryImpl(
    private val intent: Intent,
    private val prefs: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
) :
    TracksHistoryRepository {

    private var tracks = mutableListOf<Track>()

    override fun getItems(): Flow<MutableList<Track>> = flow {
        val itemsFromCache = getItemsFromCache()
        withContext(Dispatchers.IO) {
            val allIds = appDatabase.trackDao().getAllIds()
            tracks = itemsFromCache
            tracks.forEach {
                if (it.trackId in allIds) {
                    it.isFavorite = true
                }
            }
        }
        Log.d("tracks", "${tracks.size}")
        emit(tracks)
    }

    override fun clearHistory() {
        prefs.edit().clear().apply()
    }

    override fun addTrackToHistory(track: Track) {
        tracks = getItemsFromCache()
        if (track.trackId in tracks.map { it.trackId }) {
            tracks.remove(track)
            tracks.add(0, track)
            saveTracklist(prefs, tracks)
        } else {
            tracks.add(0, track)
            saveTracklist(prefs, tracks)
        }
    }

    override fun getTrackFromIntent(): Track {

        val extras: Bundle? = intent.extras
        val jsonTrack: String? = extras?.getString(Constant.CHOSEN_TRACK)
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
        return gson.fromJson(
            json, object : TypeToken<MutableList<Track>>() {}.type
        )
    }

    private fun createJsonFromTracksList(tracks: MutableList<Track>?): String {
        return gson.toJson(tracks)
    }

}




