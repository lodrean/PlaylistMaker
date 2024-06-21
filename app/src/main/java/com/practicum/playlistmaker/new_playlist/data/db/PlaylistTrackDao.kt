package com.practicum.playlistmaker.new_playlist.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(track: PlaylistTrackEntity)

    @Query("SELECT * FROM track_table")
    fun getTracks(): List<PlaylistTrackEntity>
}