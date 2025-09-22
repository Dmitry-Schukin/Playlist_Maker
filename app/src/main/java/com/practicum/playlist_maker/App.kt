package com.practicum.playlist_maker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class App: Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val s = getSharedPreferences(SETTINGS,MODE_PRIVATE)
            .getString(KEY_FOR_THEME,null)
        darkTheme = if(s!=null) Gson().fromJson(s, object : TypeToken<Boolean>() {}.type)
                    else isCurrentThemeDark()

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
    }

    fun isCurrentThemeDark(): Boolean{
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) return true
        else return false
    }
    companion object{
        const val SETTINGS = "light_dark_themes"
        const val KEY_FOR_THEME="key_for_light_dark_switch"
        const val KEY_FOR_HISTORY="key_for_track_parameters"
    }
}

