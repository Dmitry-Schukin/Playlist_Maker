package com.practicum.playlist_maker.data

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlist_maker.domain.Track
import androidx.core.content.edit


class HistorySharedPrefsManager (private val context: Context): SharedPreferences<List<Track>> {
    companion object {
        const val TRACK_HISTORY= "track_history"
        const val TRACK_TEXT_KEY = "key_for_track_parameters"
    }
    val sharedPreferences = context.getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun save(objectForSaving: List<Track>) {
        val json = gson.toJson(objectForSaving.toTypedArray())
        sharedPreferences.edit { putString(TRACK_TEXT_KEY, json) }
    }

    override fun getSavedObject(): List<Track> {
        var json = sharedPreferences.getString(TRACK_TEXT_KEY, "").toString()
        if (json.isEmpty()) {
            sharedPreferences.edit {
                putString(TRACK_TEXT_KEY, gson.toJson(emptyArray<Track>()))
            }
            json = sharedPreferences.getString(TRACK_TEXT_KEY, "").toString()
        }
        val array = gson.fromJson(json, Array<Track>::class.java)
        val historyTrackList = ArrayList(array.toList())
        return historyTrackList
    }
    override fun clear(){
        sharedPreferences.edit().clear().apply()
    }
}