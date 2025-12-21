package com.practicum.playlist_maker.library.data.impl


import com.practicum.playlist_maker.common.data.db.AppDatabase
import com.practicum.playlist_maker.common.data.db.converters.PlaylistDbConvertor
import com.practicum.playlist_maker.common.data.db.entity.PlaylistEntity
import com.practicum.playlist_maker.library.domain.api.PlaylistRepository
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.search.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
): PlaylistRepository {

    override suspend fun createNewPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().insertNewPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().deletePlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<Resource<List<Playlist>>> = flow {
        try {
            val playlists = appDatabase.playlistDao().getAllPlaylists()
            val requestResult = Resource.Success(convertFromPlaylistEntity(playlists))
            emit(requestResult)
        }catch (ex: Exception){
            emit(Resource.Error("Ошибка: $ex"))
        }
    }

    override suspend fun updateTrackListAndCount(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase
            .playlistDao()
            .updateTrackIdsAndCount(
                playlistEntity.id,
                playlistEntity.trackIdList,
                playlistEntity.trackCount)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

}