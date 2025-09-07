package com.practicum.playlist_maker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlist_maker.creator.Creator
import com.practicum.playlist_maker.domain.use_case.ThemeSharedPrefsUseCase


class App: Application() {

    private lateinit var themeUseCase:ThemeSharedPrefsUseCase

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        themeUseCase = Creator.getThemeSharedPrefsUseCase(this)
        darkTheme = themeUseCase.executeGettingThemeState(isCurrentThemeDark())
        switchTheme(darkTheme)
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        themeUseCase.executeSavingThemeState(darkTheme)
    }

    fun isCurrentThemeDark(): Boolean{
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) return true
        else return false
    }
}