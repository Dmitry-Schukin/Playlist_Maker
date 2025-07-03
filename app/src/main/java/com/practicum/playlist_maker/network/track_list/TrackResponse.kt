package com.practicum.playlist_maker.network.track_list

import com.google.gson.annotations.SerializedName
import com.practicum.playlist_maker.model.Track

class TrackResponse (@SerializedName("results") val songs: ArrayList<Track>)