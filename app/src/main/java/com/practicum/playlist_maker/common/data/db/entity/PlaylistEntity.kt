package com.practicum.playlist_maker.common.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String,
    val trackIdList: String,
    val trackCount: Int
)