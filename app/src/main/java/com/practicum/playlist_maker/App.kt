package com.practicum.playlist_maker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlist_maker.creator.Creator

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        val settingsInteractor = Creator.provideSettingsInteractor(applicationContext)
        val darkTheme = settingsInteractor.getTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        settingsInteractor.saveThemeModeValue(darkTheme)
    }

    companion object{
        const val KEY_FOR_THEME="key_for_light_dark_switch"
        const val KEY_FOR_HISTORY="key_for_track_parameters"


    }
}

