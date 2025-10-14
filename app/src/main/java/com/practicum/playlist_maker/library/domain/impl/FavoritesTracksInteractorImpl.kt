package com.practicum.playlist_maker.library.domain.impl

import com.practicum.playlist_maker.library.domain.api.FavoritesTracksInteractor
import com.practicum.playlist_maker.library.domain.api.FavoritesTracksRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import java.util.concurrent.Executors

class FavoritesTracksInteractorImpl(private val repository: FavoritesTracksRepository):
    FavoritesTracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun getFavorites(consumer: FavoritesTracksInteractor.FavoritesConsumer) {
        executor.execute {
            when (val resource = repository.getTracks()) {
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