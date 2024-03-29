package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.ItunesApiService
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.SHARED_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    /* single{
         androidContext()
             .getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
     }

     factory { Gson() }
     */
    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }
}