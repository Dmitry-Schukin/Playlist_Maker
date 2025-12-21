package com.practicum.playlist_maker.common.data.db.converters

import com.practicum.playlist_maker.common.data.db.entity.PlaylistEntity
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor() {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath,
            Gson().toJson(playlist.trackIdList),
            playlist.trackCount
        )
    }
    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.id,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.imagePath,
            Gson().fromJson(playlistEntity.trackIdList, object : TypeToken<List<String>>() {}.type),
            playlistEntity.trackCount
        )
    }
}