package com.practicum.playlist_maker.library.ui.fragments

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.library.domain.api.FavoritesTracksInteractor
import com.practicum.playlist_maker.search.domain.model.Track

class FavoritesTracksViewModel(private val context: Context,
                               private val favoritesInteractor: FavoritesTracksInteractor): ViewModel() {
    private val stateLiveData = MutableLiveData<FragmentState>()
    fun observeState(): LiveData<FragmentState> = stateLiveData
    private val handler = Handler(Looper.getMainLooper())

    fun showFavoritesTracks(){
        favoritesInteractor.getFavorites(
            object : FavoritesTracksInteractor.FavoritesConsumer {
                override fun consume(favoritesTrack: List<Track>?, errorMessage: String?) {
                    handler.post {
                        val tracks = mutableListOf<Track>()
                        if (favoritesTrack != null) {
                            tracks.addAll(favoritesTrack)
                        }
                        when {
                            errorMessage != null -> {
                                renderState(
                                    FragmentState.Error(
                                        errorMessage = context.getString(R.string.media_library_is_empty),
                                    )
                                )

                            }

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
            })

    }
    private fun renderState(state: FragmentState) {
        stateLiveData.postValue(state)
    }
}