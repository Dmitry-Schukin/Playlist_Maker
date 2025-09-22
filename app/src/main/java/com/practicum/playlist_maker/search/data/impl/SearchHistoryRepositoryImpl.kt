package com.practicum.playlist_maker.search.data.impl

import android.util.Log
import com.practicum.playlist_maker.common.data.StorageClient
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track

class SearchHistoryRepositoryImpl (
    private val storage: StorageClient<ArrayList<Track>>
): SearchHistoryRepository {

        override fun saveToHistory(track: Track) {
            /*Максимальное количество треков в истории может достигать 10.
                       Условия:
                           -Количество треков < 10 и трек не повторяется - добавляется трек, список увеличивается на 1;
                           -Количество треков < 10 и трек повторяется - после удаления количество треков по прежнему < 10,
                       добавляем в начало повторившийся трек, количество треков вернулось к начальному значению;
                           -Количество треков = 10 и трек не повторяется - данный вариант не удовлетворяет условию
                       trackList.size<10, поэтому переходим в else,удаляем 10 элемент и добавляем новый трек;
                           -Количество треков = 10 и трек повторяется - после удаления становится 9 треков, добавляем в
                       начало повторившийся трек, список снова = 10.*/
            var trackList = storage.getSavedObject()?: arrayListOf()
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
            storage.save(trackList)
    }

    override fun getHistory(): Resource<List<Track>> {
        val tracks = storage.getSavedObject()
        if(tracks!=null){
            return Resource.Success(tracks)
        }else{
            Log.e("SharedPreferenceRequest", "В Track History SharedPreference история треков отсутствует")
            storage.save(arrayListOf())
            return Resource.Error("История треков пуста")
        }
    }

    override fun clearHistory() {
        storage.clear()
    }


}