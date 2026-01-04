package com.practicum.playlist_maker.playlist_creator.ui


interface CreatorPlaylistState {
    data class ReadyToCreate(
        val imagePath: String,
        val playlistTitle: String,
        val playlistDescription:String): CreatorPlaylistState
    object EmptyInputField: CreatorPlaylistState
    object PlaylistCreated:CreatorPlaylistState
    object PlaylistUpdated:CreatorPlaylistState
}