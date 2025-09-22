package com.practicum.playlist_maker.settings.domain.impl

import com.practicum.playlist_maker.settings.domain.api.SettingsRepository
import com.practicum.playlist_maker.settings.domain.api.SettingsThemeModeInteractor

class SettingsThemeModeInteractorImpl(
    private val repository: SettingsRepository
) : SettingsThemeModeInteractor {
    override fun getTheme(): Boolean{
       return repository.getValue()
    }

    override fun saveThemeModeValue(isThemeModeValueDark: Boolean) {
        repository.saveValue(isThemeModeValueDark)
    }

    override fun clearThemeValue() {
        repository.clear()
    }
}