package com.practicum.playlist_maker.data.network

import com.practicum.playlist_maker.data.NetworkClient
import com.practicum.playlist_maker.data.dto.Response
import com.practicum.playlist_maker.data.dto.ResponseTypeEnum


class TrackNetworkClient : NetworkClient{

    override fun doRequest(expression: String): Response {

        val response = RetrofitClient.trackService.getTrack(expression).execute()
        val body = response.body() ?: Response()
        return body.apply { resultCode = ResponseTypeEnum.setResponseType(response.code()) }
    }
}


