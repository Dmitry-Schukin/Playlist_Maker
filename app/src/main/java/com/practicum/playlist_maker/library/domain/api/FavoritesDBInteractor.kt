package com.practicum.playlist_maker.library.domain.api

import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesDBInteractor {
    suspend fun makeTrackAsFavorite(track: Track)
    suspend fun deleteTrackFromFavorites(track: Track)
    fun getAllFavoriteTracks(): Flow<Pair<List<Track>?, String?>>
}