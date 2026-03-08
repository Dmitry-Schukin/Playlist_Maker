package com.practicum.playlist_maker.playlist_tracks.ui

import com.practicum.playlist_maker.search.domain.model.Track

interface PlaylistTrackState {
    data class Content(
        val title:String,
        val description:String,
        val imagePath: String,
        val list: List<Track>,
        val duration: Int,
        val trackCount: Int
    ) : PlaylistTrackState
    data class Empty(
        val title:String,
        val description:String,
        val imagePath: String,
        val message: String,
        val duration: Int,
        val trackCount: Int
    ) : PlaylistTrackState
}