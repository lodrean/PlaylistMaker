package com.practicum.playlistmaker.searchretrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") expression: String): Call<TracksResponse>
}

