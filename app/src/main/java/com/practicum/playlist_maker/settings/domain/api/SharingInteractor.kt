package com.practicum.playlist_maker.settings.domain.api

import com.practicum.playlist_maker.settings.domain.model.EmailData

interface SharingInteractor {
    fun shareApp(): String
    fun openSupport(): EmailData
    fun openTerms(): String

}