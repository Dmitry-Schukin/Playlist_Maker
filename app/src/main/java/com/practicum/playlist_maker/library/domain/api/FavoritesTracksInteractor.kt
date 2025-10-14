package com.practicum.playlist_maker.library.domain.api

import com.practicum.playlist_maker.search.domain.model.Track

interface FavoritesTracksInteractor {
    fun getFavorites(consumer: FavoritesConsumer)
    interface FavoritesConsumer {
        fun consume(favoritesTrack: List<Track>?, errorMessage: String?)
    }
}