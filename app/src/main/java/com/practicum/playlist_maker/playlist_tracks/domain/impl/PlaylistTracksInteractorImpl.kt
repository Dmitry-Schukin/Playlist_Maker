package com.practicum.playlist_maker.playlist_tracks.domain.impl


import com.practicum.playlist_maker.playlist_tracks.domain.PlaylistTracksInteractor
import com.practicum.playlist_maker.playlist_tracks.domain.PlaylistTracksRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistTracksInteractorImpl(private val repository: PlaylistTracksRepository): PlaylistTracksInteractor{
    override suspend fun insertNewTrackIntoPlaylist(
        track: Track,
        playlistId: Long
    ) {
        return repository.insertNewTrackIntoPlaylist(track, playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(
        track: Track,
        playlistId: Long
    ) {
        return repository.deleteTrackFromPlaylist(track,playlistId)
    }

    override fun getTrackCountFromSpecificPlaylist(
        trackId: String,
        playlistId: Long
    ): Flow<Pair<Int?, String?>> {
        return repository.getTrackCountFromSpecificPlaylist(trackId,playlistId).map {result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun getAllTrackFromSpecificPlaylist(playlistId: Long): Flow<Pair<List<Track>?, String?>> {
        return repository.getAllTrackFromSpecificPlaylist(playlistId).map {result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun getPlaylistTracksTotalTime(playlistId: Long): Flow<Pair<Long?, String?>> {
        return repository.getPlaylistTracksTotalTime(playlistId).map {result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun isThereTrackInSomePlaylist(trackId: String): Flow<Pair<List<Long>?, String?>> {
        return repository.isThereTrackInSomePlaylist(trackId).map {result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}