package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track

interface TrackRepository {
    fun searchTrack(expression: String): Resource<List<Track>>
}