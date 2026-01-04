package com.practicum.playlist_maker.common.data.db.converters

import com.practicum.playlist_maker.common.data.db.entity.PlaylistTrackEntity
import com.practicum.playlist_maker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale


class PlaylistTracksDbConvertor() {
    fun map(track: Track, playlistId:Long): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.artistName,
            track.trackName,
            stringFormatToTimeMillis(track.trackTimeMillis),
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            System.currentTimeMillis().toString(),
            playlistId
        )
    }
    fun map(playlistTrackEntity: PlaylistTrackEntity): Track {
        return Track(
            playlistTrackEntity.trackId,
            playlistTrackEntity.artistName,
            playlistTrackEntity.trackName,
            convertTimeMillisToPatternFormat(playlistTrackEntity.trackTimeMillis),
            playlistTrackEntity.artworkUrl100,
            playlistTrackEntity.collectionName,
            playlistTrackEntity.releaseDate,
            playlistTrackEntity.primaryGenreName,
            playlistTrackEntity.country,
            playlistTrackEntity.previewUrl,
        )
    }

    private fun stringFormatToTimeMillis(timeString:String):Long{
        val parts = timeString.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            return ((minutes * 60L) + seconds)*1000
        }
        return 0L
    }
    private fun convertTimeMillisToPatternFormat(timeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }
}