package com.practicum.playlist_maker.search.ui

import com.practicum.playlist_maker.search.domain.model.Track

sealed interface SearchState {
    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class Error(
        val errorMessage: String
    ) : SearchState

    data class Empty(
        val message: String
    ) : SearchState

    data class History(
        val tracks: List<Track>
    ): SearchState
}