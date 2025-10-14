package com.practicum.playlist_maker.library.data.impl

import android.util.Log
import com.practicum.playlist_maker.library.domain.api.PlaylistRepository
import com.practicum.playlist_maker.search.domain.model.Resource

class PlaylistRepositoryImpl(): PlaylistRepository {
    override fun getPlaylists(): Resource<List<Any>>{
            val list = mutableListOf<Any>()
            if(list!=null){
                return Resource.Success(list)
            }else{
                Log.e("PlaylistsRequest", "Список плейлистов отсутствует")
                return Resource.Error("")
            }

    }
}