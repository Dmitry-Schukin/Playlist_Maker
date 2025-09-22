package com.practicum.playlist_maker.search.data.dto

data class TrackDto (val trackId: String,
                val artistName: String,
                val trackName: String,
                val trackTimeMillis: Int,
                val artworkUrl100: String,
                val collectionName: String,
                val releaseDate: String,
                val primaryGenreName: String,
                val country: String,
                val previewUrl: String) {

}