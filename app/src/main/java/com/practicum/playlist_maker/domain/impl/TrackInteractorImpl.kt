package com.practicum.playlist_maker.domain.impl


import com.practicum.playlist_maker.domain.api.TrackInteractor
import com.practicum.playlist_maker.domain.api.TrackRepository
import java.util.concurrent.Executors


class TrackInteractorImpl(private val repository: TrackRepository): TrackInteractor{
    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(expression: String, consumer: TrackInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTrack(expression))

        }
    }
}