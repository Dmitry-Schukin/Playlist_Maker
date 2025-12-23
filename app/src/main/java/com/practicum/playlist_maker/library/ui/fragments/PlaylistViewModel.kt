package com.practicum.playlist_maker.library.ui.fragments

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.library.domain.api.PlaylistInteractor
import com.practicum.playlist_maker.library.domain.model.Playlist
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PlaylistViewModel(private val context: Context,
                        private val playlistsInteractor: PlaylistInteractor): ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData

    fun showPlaylists(){
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .catch { exception -> Log.d("FlowGettingPlaylistError", "$exception") }
                .collect { pair ->
                    val list = mutableListOf<Playlist>()
                    if (pair.first != null) {
                        list.addAll(pair.first!!)
                    }
                    when {
                        list.isEmpty() -> {
                            renderState(
                                PlaylistState.Empty(
                                    message = context.getString(R.string.media_library_is_empty),
                                )
                            )
                        }
                        else -> {
                            renderState(
                                PlaylistState.Content(
                                    list = list,
                                )
                            )
                        }
                    }
                }
        }
    }
    private fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }

}