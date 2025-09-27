package com.practicum.playlist_maker.settings.domain.api

import com.practicum.playlist_maker.settings.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink():String
    fun openEmail(): EmailData
    fun openLink():String
}