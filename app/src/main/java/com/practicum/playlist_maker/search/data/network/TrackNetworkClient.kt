package com.practicum.playlist_maker.search.data.network

import android.util.Log
import com.practicum.playlist_maker.common.data.NetworkClient
import com.practicum.playlist_maker.search.data.dto.Response
import com.practicum.playlist_maker.search.data.dto.ResponseTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackNetworkClient (private val trackService:ItunesApiService): NetworkClient {
    override suspend fun doRequest(expression: String): Response {

        return withContext(Dispatchers.IO) {
            try {
                val response = trackService.getTrack(expression)
                response.apply { resultCode = ResponseTypeEnum.SUCCESS}
            } catch (e: Exception) {
                Log.e("RequestCodeFromServer", "Код ошибки сервера $e")
                Response().apply { resultCode = ResponseTypeEnum.UNKNOWN}
            }
        }

    }

}