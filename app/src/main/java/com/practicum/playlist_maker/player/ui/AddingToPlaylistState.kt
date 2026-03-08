package com.practicum.playlist_maker.player.ui


interface AddingToPlaylistState {
    object Default: AddingToPlaylistState
    data class AlreadyInPlaylist(val playlistId:Long) : AddingToPlaylistState
    data class JustAddedInPlaylist(val playlistId:Long) : AddingToPlaylistState
}