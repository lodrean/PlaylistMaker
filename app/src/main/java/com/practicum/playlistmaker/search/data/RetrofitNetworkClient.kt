package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.Constant.Companion.BAD_REQUEST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(ItunesApiService::class.java)

    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TracksSearchRequest) {
                val resp = imdbService.searchTracks(dto.expression).execute()

                val body = resp.body() ?: Response()

                return body.apply { resultCode = resp.code() }
            } else {
                return Response().apply { resultCode = BAD_REQUEST }
            }
        } catch (Ex: Exception) {
            return Response().apply { resultCode = BAD_REQUEST }
        }
    }
}