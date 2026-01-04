package com.practicum.playlist_maker.playlist_tracks.domain.impl

import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.playlist_tracks.domain.PlaylistSharingExternalNavigator
import com.practicum.playlist_maker.playlist_tracks.domain.SharingPlaylistInteractor
import com.practicum.playlist_maker.search.domain.model.Track

class SharingPlaylistInteractorImpl(
    private val externalNavigator: PlaylistSharingExternalNavigator): SharingPlaylistInteractor {
    override fun sharePlaylist(
        playlist: Playlist,
        tracks: List<Track>
    ): String {
        return externalNavigator.sharePlaylist(playlist,tracks)
    }
}