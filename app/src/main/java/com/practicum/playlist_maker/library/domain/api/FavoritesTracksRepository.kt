package com.practicum.playlist_maker.library.domain.api

import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track

interface FavoritesTracksRepository {
    fun getTracks():Resource<List<Track>>
}