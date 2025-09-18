package com.practicum.playlist_maker.domain

class RequestResult(
    val success: Boolean,
    val trackList:List<Track>
) {
}