package com.practicum.playlist_maker.search.domain.impl

import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.search.domain.model.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {
    override fun getHistory(consumer: SearchHistoryInteractor.HistoryConsumer) {
        consumer.consume(repository.getHistory())
    }

    override fun saveToHistory(track: Track) {
        repository.saveToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}