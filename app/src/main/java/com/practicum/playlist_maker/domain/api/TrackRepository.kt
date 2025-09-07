package com.practicum.playlist_maker.domain.api

import com.practicum.playlist_maker.domain.RequestResult

interface TrackRepository {
    fun searchTrack(expression: String): RequestResult
}