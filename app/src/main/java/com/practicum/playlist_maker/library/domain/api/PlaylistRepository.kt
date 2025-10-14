package com.practicum.playlist_maker.library.domain.api

import com.practicum.playlist_maker.search.domain.model.Resource


interface PlaylistRepository {
    fun getPlaylists():Resource<List<Any>>

}