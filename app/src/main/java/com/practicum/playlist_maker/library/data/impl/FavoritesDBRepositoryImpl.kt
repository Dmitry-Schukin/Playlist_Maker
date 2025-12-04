package com.practicum.playlist_maker.library.data.impl

import com.practicum.playlist_maker.common.data.db.AppDatabase
import com.practicum.playlist_maker.common.data.db.converters.TrackDbConvertor
import com.practicum.playlist_maker.common.data.db.entity.TrackEntity
import com.practicum.playlist_maker.library.domain.api.FavoritesDBRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesDBRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoritesDBRepository {

    override suspend fun makeTrackAsFavorite(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().insertNewTrack(trackEntity)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    override fun getAllFavoriteTracks(): Flow<Resource<List<Track>>> = flow {
        try {
            val tracks = appDatabase.trackDao().getTracks()
            val requestResult = Resource.Success(convertFromTrackEntity(tracks))
            emit(requestResult)
        }catch (ex: Exception){
            emit(Resource.Error("Ошибка: $ex"))
        }

    }
    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {

        val newList = tracks.sortedByDescending{ it.timestampMillis.toLong() }
        return newList.map { track -> trackDbConvertor.map(track) }
    }
}