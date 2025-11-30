package com.practicum.playlist_maker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.search.domain.model.Resource
import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.TrackInteractor
import com.practicum.playlist_maker.utils.debounce
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel (private val context: Context,
                       private val trackListInteractor: TrackInteractor,
                       private val trackHistoryInteractor: SearchHistoryInteractor): ViewModel(){
    private var latestSearchText: String? = null
    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData
    private val handler = Handler(Looper.getMainLooper())

    //region Debounce
    private val trackSearchDebounce = debounce<String>(SEARCH_TRACK_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        searchRequest(changedText)
    }
    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }
    //endregion

    fun searchRequest(newSearchText: String) {
        if (!newSearchText.isNullOrEmpty()) {
            renderState(
                SearchState.Loading
            )
            if (isConnected()) {
                viewModelScope.launch {
                    trackListInteractor
                        .searchTrack(newSearchText)
                        .catch { exception -> Log.d("FlowError", "$exception")}
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
                renderState(
                    SearchState.Error(
                        errorMessage = context.getString(R.string.problem_with_network),
                    )
                )
            }
        } else {
            getHistoryList()
        }
    }

    fun getHistoryList(){
        trackHistoryInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer{
            override fun consume(searchHistory: Resource<List<Track>>) {
                handler.post {
                    val tracksHistory = mutableListOf<Track>()
                    if(searchHistory.data!=null){
                        tracksHistory.clear()
                        tracksHistory.addAll(searchHistory.data)
                    }
                    renderState(
                        SearchState.History(
                            tracksHistory
                        )
                    )
                }
            }
        })
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
    companion object {
        private const val SEARCH_TRACK_DEBOUNCE_DELAY = 2000L
    }
}