package com.practicum.playlist_maker.library.domain.api

import com.practicum.playlist_maker.library.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createNewPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updateTrackListAndCount(playlist: Playlist)
    fun getPlaylists():Flow<Pair<List<Playlist>?, String?>>

}