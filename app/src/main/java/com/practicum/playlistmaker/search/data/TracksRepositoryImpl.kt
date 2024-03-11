package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.Constant.Companion.SUCCESS_RESUlT
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): ArrayList<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == SUCCESS_RESUlT) {
            return ArrayList((response as TracksSearchResponse).results.map { it ->
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.url,
                    it.trackTime,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.genre,
                    it.country
                )
            })
        } else
            return arrayListOf()
        }
    }
