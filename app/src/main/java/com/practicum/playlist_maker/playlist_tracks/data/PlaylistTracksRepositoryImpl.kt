package com.practicum.playlist_maker.playlist_tracks.data

import com.practicum.playlist_maker.common.data.db.AppDatabase
import com.practicum.playlist_maker.common.data.db.converters.PlaylistTracksDbConvertor
import com.practicum.playlist_maker.common.data.db.entity.PlaylistTrackEntity
import com.practicum.playlist_maker.playlist_tracks.domain.PlaylistTracksRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistTracksRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val playlistTracksDbConvertor: PlaylistTracksDbConvertor
): PlaylistTracksRepository {
    override suspend fun insertNewTrackIntoPlaylist(
        track: Track,
        playlistId: Long
    ) {
        val playlistTrackEntity = playlistTracksDbConvertor.map(track,playlistId)
        appDatabase.playlistTracksDao().insertNewTrackToPlaylist(playlistTrackEntity)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track,playlistId: Long) {
        val playlistTrackEntity = playlistTracksDbConvertor.map(track,playlistId)
        appDatabase.playlistTracksDao().deleteTrackFromPlaylist(playlistTrackEntity)
    }

    override fun getTrackCountFromSpecificPlaylist(
        trackId: String,
        playlistId: Long
    ): Flow<Resource<Int>> = flow {
        try {
            val count = appDatabase.playlistTracksDao().getTrackCountFromSpecificPlaylist(trackId,playlistId)
            val requestResult = Resource.Success(count)
            emit(requestResult)
        }catch (ex: Exception){
            emit(Resource.Error("Ошибка: $ex"))
        }

    }

    override fun getAllTrackFromSpecificPlaylist(playlistId: Long): Flow<Resource<List<Track>>> = flow {
        try {
            val list = appDatabase.playlistTracksDao().getAllTrackFromSpecificPlaylist(playlistId)
            val requestResult = Resource.Success(convertFromPlaylistTrackEntity(list))
            emit(requestResult)
        }catch (ex: Exception){
            emit(Resource.Error("Ошибка: $ex"))
        }
    }

    override fun getPlaylistTracksTotalTime(playlistId: Long): Flow<Resource<Long>> = flow {
        try {
            val value = appDatabase.playlistTracksDao().getTotalPlaylistTime(playlistId)
            val requestResult = Resource.Success(value)
            emit(requestResult)
        }catch (ex: Exception){
            emit(Resource.Error("Ошибка: $ex"))
        }
    }

    override fun isThereTrackInSomePlaylist(trackId: String): Flow<Resource<List<Long>>> = flow {
        try {
            val list = appDatabase.playlistTracksDao().getTrackPlaylistIfThereIs(trackId)
            val requestResult = Resource.Success(list)
            emit(requestResult)
        }catch (ex: Exception){
            emit(Resource.Error("Ошибка: $ex"))
        }
    }

    private fun convertFromPlaylistTrackEntity(tracks: List<PlaylistTrackEntity>): List<Track> {
        val newList = tracks.sortedByDescending{ it.timestampMillis.toLong() }
        return newList.map { track -> playlistTracksDbConvertor.map(track) }
    }
}