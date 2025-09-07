package com.practicum.playlist_maker.data.dto

import com.google.gson.annotations.SerializedName


class TrackSearchResponse (val searchType: String,
                           val expression: String,
                           @SerializedName("results") val songs: List<TrackDto>) : Response()