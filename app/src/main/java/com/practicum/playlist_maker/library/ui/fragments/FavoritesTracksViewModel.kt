package com.practicum.playlist_maker.library.ui.fragments

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.library.domain.api.FavoritesDBInteractor
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoritesTracksViewModel(private val context: Context,
                               private val favoritesInteractor: FavoritesDBInteractor
): ViewModel() {
    private val stateLiveData = MutableLiveData<FragmentState>()
    fun observeState(): LiveData<FragmentState> = stateLiveData

    init{
        showFavoritesTracks()
    }

    fun showFavoritesTracks() {
        viewModelScope.launch {
            favoritesInteractor
                .getAllFavoriteTracks()
                .catch { exception -> Log.d("FlowGettingFavoritesError", "$exception") }
                .collect { pair ->
                    val tracks = mutableListOf<Track>()
                    if (pair.first != null) {
                        tracks.addAll(pair.first!!)
                    }
                    when {
                        tracks.isEmpty() -> {
                            renderState(
                                FragmentState.Empty(
                                    message = context.getString(R.string.media_library_is_empty),
                                )
                            )
                        }
                        else -> {
                            renderState(
                                FragmentState.Content(
                                    list = tracks,
                                )
                            )
                        }
                    }
                }
        }
    }
    private fun renderState(state: FragmentState) {
        stateLiveData.postValue(state)
    }
}