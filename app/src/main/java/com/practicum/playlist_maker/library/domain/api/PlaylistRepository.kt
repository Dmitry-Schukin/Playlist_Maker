package com.practicum.playlist_maker.library.domain.api

import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.search.domain.model.Resource
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {
    suspend fun createNewPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    fun getPlaylists():Flow<Resource<List<Playlist>>>
    suspend fun updateTrackListAndCount(playlist: Playlist)
}