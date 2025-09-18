package com.practicum.playlist_maker.domain.api

import com.practicum.playlist_maker.domain.RequestResult


interface TrackInteractor {
    fun searchTrack(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: RequestResult)
    }
}