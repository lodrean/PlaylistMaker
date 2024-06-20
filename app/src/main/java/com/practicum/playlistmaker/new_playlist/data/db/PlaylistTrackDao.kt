package com.practicum.playlistmaker.new_playlist.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface PlaylistTrackDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(track: PlaylistTrackEntity)
}