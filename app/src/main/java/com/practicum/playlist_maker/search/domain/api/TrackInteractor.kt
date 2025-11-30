package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>>
}