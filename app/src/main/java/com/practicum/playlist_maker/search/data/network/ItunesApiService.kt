package com.practicum.playlist_maker.search.data.network

import com.practicum.playlist_maker.search.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    suspend fun getTrack(@Query("term") term: String): TrackSearchResponse
}