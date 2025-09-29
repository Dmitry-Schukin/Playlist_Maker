package com.practicum.playlist_maker.search.domain.impl

import com.practicum.playlist_maker.search.domain.api.TrackInteractor
import com.practicum.playlist_maker.search.domain.api.TrackRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository): TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(expression: String, consumer: TrackInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTrack(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }
}