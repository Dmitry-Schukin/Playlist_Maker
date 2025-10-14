package com.practicum.playlist_maker.library.ui.fragments

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.library.domain.api.PlaylistInteractor
import com.practicum.playlist_maker.search.domain.model.Track

class PlaylistViewModel(private val context: Context,
                        private val playlistsInteractor: PlaylistInteractor): ViewModel() {
    private val stateLiveData = MutableLiveData<FragmentState>()
    fun observeState(): LiveData<FragmentState> = stateLiveData
    private val handler = Handler(Looper.getMainLooper())

    fun showPlaylists(){
        playlistsInteractor.getPlaylists(
            object : PlaylistInteractor.PlaylistConsumer{
                override fun consume(playlists: List<Any>?, errorMessage: String?) {
                    handler.post {
                        val playlists = mutableListOf<Track>()
                        if (playlists != null) {

                        }
                        when {
                            errorMessage != null -> {
                                renderState(
                                    FragmentState.Error(
                                        errorMessage = context.getString(R.string.playlist_was_not_created),
                                    )
                                )

                            }

                            playlists.isEmpty() -> {
                                renderState(
                                    FragmentState.Empty(
                                        message = context.getString(R.string.playlist_was_not_created),
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    FragmentState.Content(
                                        list =  playlists,
                                    )
                                )
                            }
                        }
                    }
                }
            })

    }
    private fun renderState(state: FragmentState) {
        stateLiveData.postValue(state)
    }

}