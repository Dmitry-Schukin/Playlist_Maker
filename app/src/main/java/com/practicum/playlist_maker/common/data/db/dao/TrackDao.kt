package com.practicum.playlist_maker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlist_maker.common.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNewTrack(trackEntity: TrackEntity)

    @Update(entity = TrackEntity::class, onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun updateTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM track_table")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM track_table")
    suspend fun getAllTrackId(): List<String>
}