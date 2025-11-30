package com.practicum.playlist_maker.search.domain.impl

import com.practicum.playlist_maker.search.domain.api.TrackInteractor
import com.practicum.playlist_maker.search.domain.api.TrackRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository): TrackInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTrack(expression).map { result ->
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