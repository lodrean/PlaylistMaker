package com.practicum.playlistmaker.search.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.domain.Constant.Companion.BAD_REQUEST
import com.practicum.playlistmaker.search.domain.Constant.Companion.NO_CONNECTION

class RetrofitNetworkClient(
    private val itunesService: ItunesApiService,
    private val context: Context
) : NetworkClient {

    override fun doRequest(dto: Any): Response {

        if (isConnected() == false) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = BAD_REQUEST }
        }

        val resp = itunesService.searchTracks(dto.expression).execute()

        val body = resp.body() ?: Response()

        return if (body != null) {
            body.apply { resultCode = resp.code() }
        } else {

            Response().apply { resultCode = BAD_REQUEST }
        }

    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}