package com.practicum.playlist_maker.playlist_tracks.domain

import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.search.domain.model.Track

interface PlaylistSharingExternalNavigator {
    fun sharePlaylist(playlist: Playlist, tracks:List<Track>): String
}