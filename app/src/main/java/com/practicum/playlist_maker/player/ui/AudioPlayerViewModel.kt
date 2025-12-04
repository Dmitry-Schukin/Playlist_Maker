package com.practicum.playlist_maker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.library.domain.api.FavoritesDBInteractor
import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel (track: Track,
                            private val playerInteractor: PlayerInteractor,
                            private  val dbInteractor: FavoritesDBInteractor): ViewModel() {
    companion object {
        private const val UPDATE_DELAY = 300L
    }
    private var url = track.previewUrl
    private var currentTrack:Track= track
    private var timerJob: Job? = null

    //region LiveData and Observers
    private val stateAndTimerLiveData=
        MutableLiveData<StateController>(StateController.Default())
    fun observeStateAndTime(): LiveData<StateController> = stateAndTimerLiveData

    private val favoriteState=
        MutableLiveData<Boolean>(track.isFavorite)
    fun observeFavoriteState(): LiveData<Boolean> = favoriteState
    //endregion

    init {
        preparePlayer()
        checkTrackInFavoritesList()
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
    }
    //region Player State Functions
    fun onPlayButtonClicked() {
        when(stateAndTimerLiveData.value) {
            is StateController.Playing -> {
                pausePlayer()
            }
            is StateController.Prepared, is StateController.Paused -> {
                startPlayer()
            }
            else -> {}
        }
    }
    fun preparePlayer(){
        playerInteractor.preparePlayer(url,
            {
                stateAndTimerLiveData.postValue(StateController.Prepared())
            },
            {
                timerJob?.cancel()
                stateAndTimerLiveData.postValue(StateController.Prepared())
            })
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        stateAndTimerLiveData.postValue(StateController.Playing(getCurrentPlayerPosition()))
        startTimerUpdate()
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(UPDATE_DELAY)
                stateAndTimerLiveData.postValue(StateController.Playing(getCurrentPlayerPosition()))
            }
        }
    }
    fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        stateAndTimerLiveData.postValue(StateController.Paused(getCurrentPlayerPosition()))
    }
    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentAudioTime())
    }
    fun release(){
        playerInteractor.stop()
        playerInteractor.releasePlayer()
        stateAndTimerLiveData.value = StateController.Default()
    }
    fun onPause(){
        pausePlayer()
    }
    //endregion

    //region Favorite Button Functions
    fun onFavoriteButtonClicked() {
        when(favoriteState.value) {
            true -> {
                deleteTrackFromFavorites()
                favoriteState.postValue(false)
            }
            else -> {
                makeTrackFavorite()
                favoriteState.postValue(true)
            }
        }
    }
    fun makeTrackFavorite(){
        viewModelScope.launch {
            dbInteractor.makeTrackAsFavorite(currentTrack)
        }
    }
    fun deleteTrackFromFavorites(){
        viewModelScope.launch {
            dbInteractor.deleteTrackFromFavorites(currentTrack)
        }
    }
    private fun checkTrackInFavoritesList(){
        viewModelScope.launch {
            dbInteractor.getAllFavoriteTracks().collect {
                tracks ->
                if(!tracks.first.isNullOrEmpty()) {
                    val state = tracks.first?.any { it.trackId == currentTrack.trackId }
                    currentTrack.isFavorite = state!!
                    favoriteState.postValue(state!!)
                }else{favoriteState.postValue(false)}
            }
        }
    }
    //endregion
}