package com.practicum.playlist_maker.library.data.impl

import android.util.Log
import com.practicum.playlist_maker.library.domain.api.FavoritesTracksRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track

class FavoritesTracksRepositoryImpl(): FavoritesTracksRepository {
    override fun getTracks(): Resource<List<Track>> {
        val tracks = mutableListOf<Track>()
        if(tracks!=null){
            return Resource.Success(tracks)
        }else{
            Log.e("FavoritesTracksRequest", "Список треков отсутствует")
            return Resource.Error("")
        }
    }
}