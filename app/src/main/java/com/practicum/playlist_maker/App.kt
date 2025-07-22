package com.practicum.playlist_maker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate


const val LIGHT_DARK_THEMES = "light_dark_themes"
const val EDIT_TEXT_KEY = "key_for_light_dark_switch"

class App: Application() {

    private lateinit var sharedPrefs: SharedPreferences
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(LIGHT_DARK_THEMES, MODE_PRIVATE)
        val settingsFromSharedPrefs = sharedPrefs.getBoolean(EDIT_TEXT_KEY, isCurrentThemeDark())
        darkTheme = settingsFromSharedPrefs
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
        sharedPrefs = getSharedPreferences(LIGHT_DARK_THEMES, MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(EDIT_TEXT_KEY, darkTheme).apply()
    }

    fun isCurrentThemeDark(): Boolean{
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) return true
        else return false
    }
}