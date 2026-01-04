package com.practicum.playlist_maker.playlist_tracks.domain

import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistTracksInteractor {
    suspend fun insertNewTrackIntoPlaylist(track: Track,playlistId:Long)
    suspend fun deleteTrackFromPlaylist(track: Track,playlistId:Long)
    fun getTrackCountFromSpecificPlaylist(trackId: String, playlistId: Long): Flow<Pair<Int?, String?>>
    fun getAllTrackFromSpecificPlaylist(playlistId: Long):Flow<Pair<List<Track>?,String?>>
    fun getPlaylistTracksTotalTime(playlistId: Long): Flow<Pair<Long?, String?>>
    fun isThereTrackInSomePlaylist(trackId: String):Flow<Pair<List<Long>?,String?>>
}