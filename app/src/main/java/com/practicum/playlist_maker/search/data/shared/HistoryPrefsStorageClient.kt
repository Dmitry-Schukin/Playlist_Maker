package com.practicum.playlist_maker.search.data.shared

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlist_maker.common.data.StorageClient
import java.lang.reflect.Type

class HistoryPrefsStorageClient <T>(
    private val context: Context,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {

    companion object {
        const val TRACK_HISTORY= "track_history"
    }
    val sharedPreferences = context.getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
    private val gson = Gson()



    override fun save(objectForSaving: T) {
        sharedPreferences.edit { putString(dataKey, gson.toJson(objectForSaving, type)) }
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