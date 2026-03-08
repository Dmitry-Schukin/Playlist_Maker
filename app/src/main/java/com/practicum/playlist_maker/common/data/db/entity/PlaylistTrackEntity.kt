package com.practicum.playlist_maker.common.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_tracks_table",
    foreignKeys = [ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE
    )])
data class PlaylistTrackEntity(
                                @PrimaryKey(autoGenerate = false)
                                val trackId: String,
                                val artistName: String,
                                val trackName: String,
                                val trackTimeMillis: Long,
                                val artworkUrl100: String,
                                val collectionName: String,
                                val releaseDate: String,
                                val primaryGenreName: String,
                                val country: String,
                                val previewUrl: String,
                                val timestampMillis: String,
                                val playlistId:Long){

}
