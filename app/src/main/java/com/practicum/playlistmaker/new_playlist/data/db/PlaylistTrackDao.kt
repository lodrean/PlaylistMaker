package com.practicum.playlistmaker.new_playlist.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_track_table WHERE trackId = :trackId")
    fun delete(trackId: String)

    @Query("SELECT * FROM playlist_track_table")
    fun getTracks(): List<PlaylistTrackEntity>
}