package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun  getHistory(): Flow<Resource<List<Track>>>
    fun clearHistory()
}