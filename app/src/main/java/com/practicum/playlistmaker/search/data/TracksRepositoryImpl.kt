package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<ArrayList<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                with(response as TracksSearchResponse) {
                    val data = ArrayList(results.map {
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
                    val allIds = appDatabase.trackDao().getAllIds()
                    data.map { if (it.trackId in allIds) it.isFavorite = true }
                    emit(Resource.Success(data))
                }


            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}
