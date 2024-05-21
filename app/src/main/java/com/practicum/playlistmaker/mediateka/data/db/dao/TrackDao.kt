package com.practicum.playlistmaker.mediateka.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediateka.data.db.TrackEntity
import com.practicum.playlistmaker.search.domain.Track

@Dao
interface TrackDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(track: TrackEntity)

    @Delete()
    fun delete(track: TrackEntity)

    @Query("SELECT * FROM track_table")
    fun getTracksAll(): List<TrackEntity>

    @Query("SELECT trackId FROM track_table")
    fun getAllIds(): List<Int>

}