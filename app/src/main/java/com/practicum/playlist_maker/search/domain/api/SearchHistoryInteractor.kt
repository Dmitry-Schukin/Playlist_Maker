package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track

interface SearchHistoryInteractor {

    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(track: Track)
    fun clearHistory()

    interface HistoryConsumer {
        fun consume(searchHistory: Resource<List<Track>>)
    }
}