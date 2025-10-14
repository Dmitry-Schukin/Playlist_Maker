package com.practicum.playlist_maker.library.domain.api

interface PlaylistInteractor {
    fun getPlaylists(consumer: PlaylistConsumer)
    interface PlaylistConsumer {
        fun consume(playlists: List<Any>?, errorMessage: String?)
    }
}