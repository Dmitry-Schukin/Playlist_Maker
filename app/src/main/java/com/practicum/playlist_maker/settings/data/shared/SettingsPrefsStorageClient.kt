package com.practicum.playlist_maker.settings.data.shared

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlist_maker.common.data.StorageClient
import java.lang.reflect.Type

class SettingsPrefsStorageClient <T>(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {

    override fun save(objectForSaving: T) {
        sharedPreferences.edit().putString(dataKey, gson.toJson(objectForSaving, type)).apply()
    }

    override fun getSavedObject(): T? {
        val dataJson = sharedPreferences.getString(dataKey, null)
        if (dataJson == null) {
            return null
        } else {
            return gson.fromJson(dataJson, type)
        }
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}