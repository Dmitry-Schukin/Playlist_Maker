package com.practicum.playlist_maker.playlist_tracks.domain

import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistTracksRepository {
    suspend fun insertNewTrackIntoPlaylist(track: Track,playlistId:Long)
    suspend fun deleteTrackFromPlaylist(track: Track,playlistId:Long)
    fun getTrackCountFromSpecificPlaylist(trackId: String, playlistId: Long): Flow<Resource<Int>>
    fun getAllTrackFromSpecificPlaylist(playlistId: Long):Flow<Resource<List<Track>>>
    fun getPlaylistTracksTotalTime(playlistId: Long): Flow<Resource<Long>>
    fun isThereTrackInSomePlaylist(trackId: String):Flow<Resource<List<Long>>>

}