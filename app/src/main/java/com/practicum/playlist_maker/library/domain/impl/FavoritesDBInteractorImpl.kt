package com.practicum.playlist_maker.library.domain.impl

import com.practicum.playlist_maker.library.domain.api.FavoritesDBInteractor
import com.practicum.playlist_maker.library.domain.api.FavoritesDBRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesDBInteractorImpl(private  val favoritesDBRepository: FavoritesDBRepository)
    : FavoritesDBInteractor {
    override suspend fun makeTrackAsFavorite(track: Track) {
        return favoritesDBRepository.makeTrackAsFavorite(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        return favoritesDBRepository.deleteTrackFromFavorites(track)
    }

    override fun getAllFavoriteTracks(): Flow<Pair<List<Track>?, String?>> {
        return favoritesDBRepository.getAllFavoriteTracks().map {result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}