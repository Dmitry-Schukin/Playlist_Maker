package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {

    fun getHistory(): Flow<Pair<List<Track>?, String?>>
    fun saveToHistory(track: Track)
    fun clearHistory()

}