package com.practicum.playlist_maker.presentation.search

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.SearchActivity
import com.practicum.playlist_maker.model.Track



class SearchHistory (val sharedPrefsTrackHist: SharedPreferences) {

    companion object {
        const val TRACK_HISTORY= "track_history"
        const val TRACK_TEXT_KEY = "key_for_track_parameters"
    }
    fun clearHistory(){
        sharedPrefsTrackHist.edit().clear().apply()
    }
    fun addTrackToHistoryList(track: Track){
        /*Максимальное количество треков в истории может достигать 10.
            Условия:
                -Количество треков < 10 и трек не повторяется - добавляется трек, список увеличивается на 1;
                -Количество треков < 10 и трек повторяется - после удаления количество треков по прежнему < 10,
            добавляем в начало повторившийся трек, количество треков вернулось к начальному значению;
                -Количество треков = 10 и трек не повторяется - данный вариант не удовлетворяет условию
            trackList.size<10, поэтому переходим в else,удаляем 10 элемент и добавляем новый трек;
                -Количество треков = 10 и трек повторяется - после удаления становится 9 треков, добавляем в
            начало повторившийся трек, список снова = 10.*/
        val json = sharedPrefsTrackHist.getString(TRACK_TEXT_KEY,"").toString()
        var trackList = mutableListOf<Track>()
        if (!json.isNullOrEmpty()) {
            trackList = ArrayList(createTracksListFromJson(json).toList())

            val iterator = trackList.iterator()//При помощи итератора удаляем из истории трек, если он там присутствует
            while (iterator.hasNext()) {
                val trackIterator = iterator.next()
                if (trackIterator.trackId == track.trackId) {
                    iterator.remove()
                }
            }
            if(trackList.size<10){
                trackList.add(0, track)
            }else {
                trackList.removeAt(9)
                trackList.add(0, track)
            }
        }
        sharedPrefsTrackHist.edit()
            .putString(TRACK_TEXT_KEY, createJsonFromTracksList(trackList.toTypedArray()))
            .apply()
    }

    fun getHistoryJson():String{
        val json = sharedPrefsTrackHist.getString(TRACK_TEXT_KEY,"").toString()
        return json
    }
    fun createTracksListFromJson(json: String): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }
    fun createJsonFromTracksList(tracks: Array<Track>): String {
        return Gson().toJson(tracks)
    }
}