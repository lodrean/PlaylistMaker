package com.practicum.playlistmaker.search.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    suspend fun searchTracks(@Query("term") expression: String): TracksSearchResponse
}

