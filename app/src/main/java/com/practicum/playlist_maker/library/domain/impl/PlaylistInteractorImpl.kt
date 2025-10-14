package com.practicum.playlist_maker.library.domain.impl

import com.practicum.playlist_maker.library.domain.api.PlaylistInteractor
import com.practicum.playlist_maker.library.domain.api.PlaylistRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import java.util.concurrent.Executors

class PlaylistInteractorImpl(private val repository: PlaylistRepository): PlaylistInteractor{
    private val executor = Executors.newCachedThreadPool()

    override fun getPlaylists(consumer: PlaylistInteractor.PlaylistConsumer) {
        executor.execute {
            when (val resource = repository.getPlaylists()) {
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