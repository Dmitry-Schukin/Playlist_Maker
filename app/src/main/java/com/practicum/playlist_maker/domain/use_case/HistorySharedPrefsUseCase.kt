package com.practicum.playlist_maker.domain.use_case

import com.practicum.playlist_maker.data.HistorySharedPrefsManager
import com.practicum.playlist_maker.domain.Track

class HistorySharedPrefsUseCase (
    private val historyManager: HistorySharedPrefsManager
){
    fun executeSavingHistoryList(tasks: List<Track>) {
        historyManager.save(tasks)
    }
    fun executeGettingHistoryList(): List<Track> {
        return historyManager.getSavedObject()
    }
    fun executeClear(){
        historyManager.clear()
    }
    fun executeAddingNewTrack(track:Track){
        /*Максимальное количество треков в истории может достигать 10.
                    Условия:
                        -Количество треков < 10 и трек не повторяется - добавляется трек, список увеличивается на 1;
                        -Количество треков < 10 и трек повторяется - после удаления количество треков по прежнему < 10,
                    добавляем в начало повторившийся трек, количество треков вернулось к начальному значению;
                        -Количество треков = 10 и трек не повторяется - данный вариант не удовлетворяет условию
                    trackList.size<10, поэтому переходим в else,удаляем 10 элемент и добавляем новый трек;
                        -Количество треков = 10 и трек повторяется - после удаления становится 9 треков, добавляем в
                    начало повторившийся трек, список снова = 10.*/
        var trackList = executeGettingHistoryList().toMutableList()
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
        executeSavingHistoryList(trackList)
    }
}