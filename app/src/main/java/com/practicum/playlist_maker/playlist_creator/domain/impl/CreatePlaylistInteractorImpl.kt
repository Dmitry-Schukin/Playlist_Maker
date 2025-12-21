package com.practicum.playlist_maker.playlist_creator.domain.impl

import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.playlist_creator.domain.CreatePlaylistInteractor
import com.practicum.playlist_maker.playlist_creator.domain.CreatePlaylistRepository

class CreatePlaylistInteractorImpl (private val repository: CreatePlaylistRepository):
    CreatePlaylistInteractor {

    override suspend fun createNewPlaylist(playlist: Playlist) {
        return repository.createNewPlaylist(playlist)
    }
}