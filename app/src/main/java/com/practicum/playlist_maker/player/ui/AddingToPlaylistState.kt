package com.practicum.playlist_maker.player.ui


interface AddingToPlaylistState {
    object Default: AddingToPlaylistState
    object AlreadyInPlaylist : AddingToPlaylistState
    object ThereIsNotInPlaylist : AddingToPlaylistState
    object JustAddedInPlaylist : AddingToPlaylistState
}