package com.practicum.playlist_maker.playlist_tracks.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.library.domain.api.PlaylistInteractor
import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.playlist_tracks.domain.PlaylistTracksInteractor
import com.practicum.playlist_maker.playlist_tracks.domain.SharingPlaylistInteractor
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistTracksViewModel(playlistId: Long,
                              private val playlistDbInteractor: PlaylistInteractor,
                              private val playlistTracksDbInteractor: PlaylistTracksInteractor,
                              private val sharePlaylistInteractor: SharingPlaylistInteractor): ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistTrackState>()
    fun observeState(): LiveData<PlaylistTrackState> = stateLiveData
    private var currentPlaylistID = playlistId
    private var currentPlaylist: Playlist? = null
    private var currentTrackList = mutableListOf<Track>()

    fun showTracksFromPlaylists(){
        val job = viewModelScope.launch {
            playlistDbInteractor
                .getPlaylistInfo(currentPlaylistID)
                .catch { exception -> Log.d("FlowGettingPlaylistInfoError", "$exception") }
                .collect { pair ->
                    var playlist:Playlist? = null
                    if (pair.first != null) {
                        playlist = pair.first!!
                    }
                    when {
                        playlist == null -> {
                            Playlist(-1,"","","",mutableListOf(),0)
                        }
                        else -> {
                            currentPlaylist = playlist
                        }
                    }
                }
        }
        viewModelScope.launch {
            job.join()
            playlistTracksDbInteractor
                .getAllTrackFromSpecificPlaylist(currentPlaylistID)
                .catch { exception -> Log.d("FlowGettingTracksFromPlaylistError", "$exception") }
                .collect { pair ->
                    currentTrackList.clear()
                    if (pair.first != null) {
                        currentTrackList.addAll(pair.first!!)
                    }
                    when {
                        currentTrackList.isEmpty() -> {
                            renderState(
                                PlaylistTrackState.Empty(
                                    title = currentPlaylist?.playlistName?: "",
                                    description = currentPlaylist?.playlistDescription ?: "",
                                    imagePath = currentPlaylist?.imagePath?: "",
                                    message = R.string.playlist_is_empty.toString(),
                                    duration = 0,
                                    trackCount = 0
                                )
                            )
                        }
                        else -> {
                            renderState(
                                PlaylistTrackState.Content(
                                    title = currentPlaylist?.playlistName?: "",
                                    description = currentPlaylist?.playlistDescription ?: "",
                                    imagePath = currentPlaylist?.imagePath?: "",
                                    list = currentTrackList,
                                    duration = getCurrentPlaylistDuration(currentTrackList),
                                    trackCount = currentTrackList.size
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun renderState(state: PlaylistTrackState) {
        stateLiveData.postValue(state)
    }
    fun deleteTrackFromPlaylist(track:Track){
        viewModelScope.launch {
            val newPlaylist = Playlist.deleteTrackFromPlaylist(currentPlaylist!!,track.trackId)
            currentPlaylist = newPlaylist
            playlistDbInteractor.updateTrackListAndCount(newPlaylist)
            playlistTracksDbInteractor.deleteTrackFromPlaylist(track, currentPlaylistID)
            showTracksFromPlaylists()
        }
    }
    fun deletePlaylist(){
        viewModelScope.launch {
            playlistDbInteractor.deletePlaylist(currentPlaylist!!)
        }
    }

    fun getCurrentPlaylistDuration(list: List<Track>):Int{
        if(list.isNotEmpty()) {
            val timeList = mutableListOf<Long>()
            list.forEach { item ->
                val parts = item.trackTimeMillis.split(":")
                if (parts.size == 2) {
                    val minutes = parts[0].toIntOrNull() ?: 0
                    val seconds = parts[1].toIntOrNull() ?: 0
                    timeList.add(((minutes * 60L) + seconds) * 1000)
                } else {
                    timeList.add(0L)
                }
            }
            val sum = timeList.sum()
            val value = SimpleDateFormat("mm", Locale.getDefault()).format(sum).toInt()
            return value
        }else return 0
    }
    fun getCurrentPlaylistTitle(): String{
        return currentPlaylist?.playlistName?:""
    }
    fun getCurrentPlaylist():Playlist{
        return currentPlaylist!!
    }
    fun sharePlaylist(): String{
        return sharePlaylistInteractor.sharePlaylist(currentPlaylist!!, currentTrackList)
    }
    fun trackListIsNotEmpty(): Boolean{
        var result = false
        if(currentTrackList.size>0){
            result=true
        }
        return result
    }
}