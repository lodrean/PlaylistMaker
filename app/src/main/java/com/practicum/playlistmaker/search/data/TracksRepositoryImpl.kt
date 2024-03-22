package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Resource<ArrayList<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success(ArrayList((response as TracksSearchResponse).results.map { it ->
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
                }))
            }

            else -> {
                Resource.Error("Ощибка сервера")
            }
        }
    }
}
