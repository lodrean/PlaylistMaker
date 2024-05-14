package com.practicum.playlistmaker.search.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.domain.Constant.Companion.BAD_REQUEST
import com.practicum.playlistmaker.search.domain.Constant.Companion.NO_CONNECTION
import com.practicum.playlistmaker.search.domain.Constant.Companion.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val itunesService: ItunesApiService,
    private val context: Context
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {

        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = BAD_REQUEST }
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = itunesService.searchTracks(dto.expression)
                response.apply { resultCode = SUCCESS }
            } catch (e: Throwable) {
                Response().apply { resultCode = BAD_REQUEST }
            }
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