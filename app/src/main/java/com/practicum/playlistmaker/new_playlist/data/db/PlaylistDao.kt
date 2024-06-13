package com.practicum.playlistmaker.new_playlist.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediateka.data.db.TrackEntity
import com.practicum.playlistmaker.new_playlist.domain.Playlist

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlist: PlaylistEntity)

    @Delete
    fun delete(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    fun getTracksAll(): List<PlaylistEntity>

    @Query("SELECT idList FROM playlist_table")
    fun getAllIds(): List<String>
}