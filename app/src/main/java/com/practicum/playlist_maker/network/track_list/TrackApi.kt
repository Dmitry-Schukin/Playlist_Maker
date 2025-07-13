package com.practicum.playlist_maker.network.track_list

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrackApi {
    @GET("/search?entity=song")
    fun getTrack(@Query("term") term: String): Call<TrackResponse>
}