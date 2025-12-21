package com.practicum.playlist_maker.playlist_creator.data.impl

import com.practicum.playlist_maker.common.data.db.AppDatabase
import com.practicum.playlist_maker.common.data.db.converters.PlaylistDbConvertor
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.playlist_creator.domain.CreatePlaylistRepository

class CreatePlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
): CreatePlaylistRepository {

    override suspend fun createNewPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().insertNewPlaylist(playlistEntity)
    }
}