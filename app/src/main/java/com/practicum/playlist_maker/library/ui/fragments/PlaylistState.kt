package com.practicum.playlist_maker.library.ui.fragments

import com.practicum.playlist_maker.library.domain.model.Playlist

interface PlaylistState {
        data class Content(
            val list: List<Playlist>
            ) : PlaylistState
        data class Empty(
            val message: String
            ) : PlaylistState
}