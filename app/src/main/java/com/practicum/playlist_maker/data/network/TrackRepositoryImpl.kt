package com.practicum.playlist_maker.data.network

import android.annotation.SuppressLint
import com.practicum.playlist_maker.data.dto.ResponseTypeEnum
import com.practicum.playlist_maker.data.dto.TrackSearchResponse
import com.practicum.playlist_maker.domain.RequestResult
import com.practicum.playlist_maker.domain.api.TrackRepository
import com.practicum.playlist_maker.domain.Track
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TrackRepositoryImpl (
    private val trackNetworkClient: TrackNetworkClient): TrackRepository {

    @SuppressLint("SuspiciousIndentation")
    override fun searchTrack(expression: String): RequestResult {

        val trackResponse = trackNetworkClient.doRequest(expression)

        if (trackResponse.resultCode == ResponseTypeEnum.SUCCESS) {
            val requestResult = RequestResult(
                true,
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
                        previewUrl = it.previewUrl
                    )
                })
            return requestResult
        } else {
            return RequestResult(false, emptyList())
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
}