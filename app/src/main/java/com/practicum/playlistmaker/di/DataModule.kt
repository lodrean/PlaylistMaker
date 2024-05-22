package com.practicum.playlistmaker.di

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.ItunesApiService
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.SHARED_PREFERENCES
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
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

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences(SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
    }

    factory<Gson> { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }
    single<ExternalNavigator> {
        ExternalNavigator(androidContext())
    }

    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}