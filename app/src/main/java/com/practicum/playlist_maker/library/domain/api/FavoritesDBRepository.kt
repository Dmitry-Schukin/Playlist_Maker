package com.practicum.playlist_maker.library.domain.api

import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesDBRepository {
    suspend fun makeTrackAsFavorite(track: Track)
    suspend fun deleteTrackFromFavorites(track: Track)
    fun getAllFavoriteTracks(): Flow<Resource<List<Track>>>
}