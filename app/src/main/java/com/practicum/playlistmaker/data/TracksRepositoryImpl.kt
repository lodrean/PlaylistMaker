package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Constant.Companion.SUCCESS_RESUlT
import com.practicum.playlistmaker.domain.models.Track

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
