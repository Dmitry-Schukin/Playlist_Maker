package com.practicum.playlist_maker.settings.data.impl

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlist_maker.common.data.StorageClient
import com.practicum.playlist_maker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl (private val storage: StorageClient<Boolean>):
    SettingsRepository {
    override fun saveValue(value: Boolean) {
        storage.save(value)
    }

    override fun getValue(): Boolean {
        val isDarkThemeMode = storage.getSavedObject()
        if(isDarkThemeMode!=null){
            return isDarkThemeMode
        }else{
            return isCurrentThemeDark()
        }
    }

    override fun clear() {
        storage.clear()
    }
    fun isCurrentThemeDark(): Boolean{
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) return true
        else return false
    }
}