package com.practicum.playlist_maker.settings.domain.api

interface SettingsThemeModeInteractor {
    fun getTheme(): Boolean
    fun saveThemeModeValue(isThemeModeValueDark: Boolean)
    fun clearThemeValue()

}