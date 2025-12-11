package com.practicum.playlist_maker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.TrackInteractor
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel (private val context: Context,
                       private val trackListInteractor: TrackInteractor,
                       private val trackHistoryInteractor: SearchHistoryInteractor): ViewModel(){

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    fun searchRequest(newSearchText: String) {
        if (!newSearchText.isNullOrEmpty()) {
            renderState(
                SearchState.Loading
            )

            viewModelScope.launch {
                trackListInteractor
                    .searchTrack(newSearchText)
                    .catch { exception -> Log.d("FlowSearchingError", "$exception") }
                    .collect { pair ->

                        val tracks = mutableListOf<Track>()

                        if (pair.first != null) {
                            tracks.addAll(pair.first!!)
                        }
                        when {
                            pair.second != null -> {
                                renderState(
                                    SearchState.Error(
                                        errorMessage = context.getString(R.string.problem_with_network),
                                    )
                                )

                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    SearchState.Empty(
                                        message = context.getString(R.string.nothing_was_found),
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    SearchState.Content(
                                        tracks = tracks,
                                    )
                                )
                            }
                        }
                    }
            }
        } else {
            getHistoryList()
        }
    }

    fun getHistoryList() {
        viewModelScope.launch {
            trackHistoryInteractor
                .getHistory()
                .catch { exception -> Log.d("FlowHistoryError", "$exception") }
                .collect { pair ->
                    val tracksHistory = mutableListOf<Track>()
                    if (pair.first != null) {
                        tracksHistory.clear()
                        tracksHistory.addAll(pair.first!!)
                    }else{
                        tracksHistory.clear()
                        tracksHistory.addAll(tracksHistory)
                    }
                    renderState(
                        SearchState.History(
                            tracksHistory
                        )
                    )
                }
        }
    }

    fun addNewTrackToHistoryList(newTrack: Track){
        trackHistoryInteractor.saveToHistory(newTrack)
    }
    fun clearHistory(){
        trackHistoryInteractor.clearHistory()
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()

    }
    @SuppressLint("ServiceCast")
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}