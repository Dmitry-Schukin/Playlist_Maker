package com.practicum.playlist_maker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlist_maker.common.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTracksDao {
    @Insert(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNewTrackToPlaylist(playlistTrackEntity: PlaylistTrackEntity)

    @Update(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun updateTrackInPlaylist(playlistTrackEntity: PlaylistTrackEntity)

    @Delete(entity = PlaylistTrackEntity::class)
    suspend fun deleteTrackFromPlaylist(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks_table")
    suspend fun getAllPlaylistsTracks(): List<PlaylistTrackEntity>

    @Query("SELECT trackId FROM playlist_tracks_table")
    suspend fun getAllTrackIds(): List<String>

    @Query("SELECT * FROM playlist_tracks_table WHERE playlistId = :playlistId")
    suspend fun getAllTrackFromSpecificPlaylist(playlistId: Long): List<PlaylistTrackEntity>

    @Query("SELECT COUNT(*) FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun getTrackCount(trackId: String): Int

    @Query("SELECT COUNT(*) FROM playlist_tracks_table WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun getTrackCountFromSpecificPlaylist(trackId: String, playlistId: Long): Int

    @Query("SELECT SUM(trackTimeMillis) FROM playlist_tracks_table WHERE playlistId = :playlistId")
    suspend fun getTotalPlaylistTime(playlistId: Long): Long

    @Query("SELECT playlistId FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun getTrackPlaylistIfThereIs(trackId: String): List<Long>

}