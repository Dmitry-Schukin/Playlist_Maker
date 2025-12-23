package com.practicum.playlist_maker.playlist_creator.domain

import com.practicum.playlist_maker.library.domain.model.Playlist

interface CreatePlaylistInteractor {
    suspend fun createNewPlaylist(playlist: Playlist)
}