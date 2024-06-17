package com.practicum.playlistmaker.new_playlist.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.mediateka.data.db.TrackEntity

@Dao
interface PlaylistTrackDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(track: PlaylistTrackEntity)
}