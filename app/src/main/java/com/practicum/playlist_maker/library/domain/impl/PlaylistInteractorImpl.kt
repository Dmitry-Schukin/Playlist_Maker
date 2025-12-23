package com.practicum.playlist_maker.library.domain.impl

import com.practicum.playlist_maker.library.domain.api.PlaylistInteractor
import com.practicum.playlist_maker.library.domain.api.PlaylistRepository
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.search.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class PlaylistInteractorImpl(private val repository: PlaylistRepository): PlaylistInteractor{
    override suspend fun createNewPlaylist(playlist: Playlist) {
        return repository.createNewPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        return repository.deletePlaylist(playlist)
    }

    override suspend fun updateTrackListAndCount(playlist: Playlist) {
        return repository.updateTrackListAndCount(playlist)
    }

    override fun getPlaylists(): Flow<Pair<List<Playlist>?, String?>> {
        return repository.getPlaylists().map {result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

}