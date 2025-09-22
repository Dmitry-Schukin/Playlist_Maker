package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Track

interface TrackInteractor {
    fun searchTrack(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}