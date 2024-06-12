package com.practicum.playlistmaker.util

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediateka.data.db.TrackEntity
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackDao

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}
