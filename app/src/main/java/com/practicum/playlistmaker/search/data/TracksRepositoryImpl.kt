package com.practicum.playlistmaker.search.data

import android.app.Application
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.AppDatabase
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val application: Application,
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<ArrayList<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(application.getString(R.string.check_internet)))
            }

            200 -> {
                with(response as TracksSearchResponse) {
                    val data = ArrayList(results.map {
                        Track(
                            it.trackId.toString(),
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
                    withContext(Dispatchers.IO) {
                        val allIds = appDatabase.trackDao().getAllIds()

                        data.map {
                            if (it.trackId in allIds) {
                                it.isFavorite = true
                            }
                        }
                    }
                    emit(Resource.Success(data))
                }


            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}
