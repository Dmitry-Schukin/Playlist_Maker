package com.practicum.playlist_maker.common.data

import com.practicum.playlist_maker.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(expression: String): Response
}