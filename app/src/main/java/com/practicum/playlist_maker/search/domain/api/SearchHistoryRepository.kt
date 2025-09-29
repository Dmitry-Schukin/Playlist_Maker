package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun getHistory(): Resource<List<Track>>
    fun clearHistory()
}