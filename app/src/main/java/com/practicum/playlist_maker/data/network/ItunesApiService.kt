package com.practicum.playlist_maker.data.network

import com.practicum.playlist_maker.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    fun getTrack(@Query("term") term: String): Call<TrackSearchResponse>
}