package com.practicum.playlist_maker.settings.domain.api

interface SettingsRepository {
    fun saveValue(value: Boolean)
    fun getValue(): Boolean
    fun clear()
}