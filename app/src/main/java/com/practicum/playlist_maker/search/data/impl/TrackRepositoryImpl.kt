package com.practicum.playlist_maker.search.data.impl

import android.annotation.SuppressLint
import com.practicum.playlist_maker.common.data.NetworkClient
import com.practicum.playlist_maker.common.data.db.AppDatabase
import com.practicum.playlist_maker.search.data.dto.ResponseTypeEnum
import com.practicum.playlist_maker.search.data.dto.TrackSearchResponse
import com.practicum.playlist_maker.search.domain.api.TrackRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TrackRepositoryImpl (
    private val trackNetworkClient: NetworkClient,
    private val appDatabase: AppDatabase
): TrackRepository {

    @SuppressLint("SuspiciousIndentation")
    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow {

        val trackResponse = trackNetworkClient.doRequest(expression)

            if (trackResponse.resultCode == ResponseTypeEnum.SUCCESS) {
                val favoritesId = getAllTrackId()
                val requestResult = Resource.Success(
                    (trackResponse as TrackSearchResponse).songs.map {
                        Track(
                            trackId = it.trackId,
                            artistName = it.artistName,
                            trackName = it.trackName,
                            trackTimeMillis = convertTimeMillisToPatternFormat(it.trackTimeMillis),
                            artworkUrl100 = it.artworkUrl100,
                            collectionName = it.collectionName,
                            releaseDate = convertDateToPatternFormat(it.releaseDate),
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl,
                            isFavorite = favoritesId.equals(it.trackId)
                        )
                    })
                emit(requestResult)
            } else {
                emit(Resource.Error("The response to your request came with an ${trackResponse.resultCode} code"))
            }
    }

    private fun convertTimeMillisToPatternFormat(timeMillis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis.toLong())
    }

    private fun convertDateToPatternFormat(dateFullFormat: String): String {
        if (dateFullFormat.isNotEmpty()) {
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
            val parsedDate: LocalDate = LocalDate.parse(dateFullFormat, formatter)
            return parsedDate.year.toString()
        } else {
            return ""
        }
    }
    private suspend fun getAllTrackId():List<String>{
        return appDatabase.trackDao().getAllTrackId()
    }
}