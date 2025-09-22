package com.practicum.playlist_maker.search.data.network

import com.practicum.playlist_maker.common.data.NetworkClient
import com.practicum.playlist_maker.search.data.dto.Response
import com.practicum.playlist_maker.search.data.dto.ResponseTypeEnum

class TrackNetworkClient : NetworkClient {
    override fun doRequest(expression: String): Response {
        try {
            val response = RetrofitClient.trackService.getTrack(expression).execute()
            val body = response.body() ?: Response()
            return body.apply { resultCode = ResponseTypeEnum.Companion.setResponseType(response.code()) }
        }catch (ex: Exception){
            return Response()
        }
    }

}