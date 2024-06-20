package com.practicum.playlistmaker.util

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediateka.data.db.TrackEntity
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackDao
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistDao
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistTrackDao
import com.practicum.playlistmaker.new_playlist.data.db.PlaylistTrackEntity

@Database(
    version = 1,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistTrackDao(): PlaylistTrackDao
}
