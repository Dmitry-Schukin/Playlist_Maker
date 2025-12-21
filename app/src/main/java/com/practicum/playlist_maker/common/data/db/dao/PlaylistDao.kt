package com.practicum.playlist_maker.common.data.db.dao

import android.R
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlist_maker.common.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNewPlaylist(playlistEntity: PlaylistEntity)

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT trackIdList FROM playlist_table WHERE  id = :playlistId")
    suspend fun getTrackJsonInPlaylist(playlistId: Long): String

    @Query("UPDATE playlist_table SET trackIdList= :list, trackCount= :count WHERE id = :playlistId")
    suspend fun updateTrackIdsAndCount(playlistId: Long, list: String, count: Int)
}