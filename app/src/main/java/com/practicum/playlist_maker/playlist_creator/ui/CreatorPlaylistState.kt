package com.practicum.playlist_maker.playlist_creator.ui

import com.practicum.playlist_maker.library.domain.model.Playlist

interface CreatorPlaylistState {
    data class ReadyToCreate(
        val imagePath: String,
        val playlistTitle: String,
        val playlistDescription:String): CreatorPlaylistState
    object EmptyInputField: CreatorPlaylistState
    object PlaylistCreated:CreatorPlaylistState
}