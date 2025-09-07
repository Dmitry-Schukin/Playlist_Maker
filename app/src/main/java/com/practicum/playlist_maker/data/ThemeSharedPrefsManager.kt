package com.practicum.playlist_maker.data

import android.content.Context
import androidx.core.content.edit


class ThemeSharedPrefsManager(private val context: Context): SharedPreferences<Boolean>{
    companion object{
        const val LIGHT_DARK_THEMES = "light_dark_themes"
        const val EDIT_TEXT_KEY = "key_for_light_dark_switch"
    }
    val sharedPreferences = context.getSharedPreferences(LIGHT_DARK_THEMES, Context.MODE_PRIVATE)

    override fun save(objectForSaving: Boolean) {
        sharedPreferences.edit { putBoolean(EDIT_TEXT_KEY, objectForSaving).apply() }
    }

    override fun getSavedObject(): Boolean {
       return sharedPreferences.getBoolean(EDIT_TEXT_KEY, false)
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun getSavedObjectWithDefaultParam(default:Boolean): Boolean {
        return sharedPreferences.getBoolean(EDIT_TEXT_KEY, default)
    }

}