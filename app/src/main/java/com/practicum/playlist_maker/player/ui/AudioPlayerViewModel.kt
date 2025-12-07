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
    private var currentTrack:Track = track
    private var currentFavoriteStatus: Boolean = track.isFavorite
    private var timerJob: Job? = null

    //region LiveData and Observers
    private val stateLiveData=
        MutableLiveData<StateController>(StateController.Default(currentFavoriteStatus))
    fun observeState(): LiveData<StateController> = stateLiveData
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
        when(stateLiveData.value) {
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
                stateLiveData.postValue(StateController.Prepared(currentFavoriteStatus))
            },
            {
                timerJob?.cancel()
                stateLiveData.postValue(StateController.Prepared(currentFavoriteStatus))
            })
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        stateLiveData.postValue(StateController.Playing(getCurrentPlayerPosition(),currentFavoriteStatus))
        startTimerUpdate()
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(UPDATE_DELAY)
                stateLiveData.postValue(StateController.Playing(getCurrentPlayerPosition(),currentFavoriteStatus))
            }
        }
    }
    fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        stateLiveData.postValue(StateController.Paused(getCurrentPlayerPosition(),currentFavoriteStatus))
    }
    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentAudioTime())
    }
    fun release(){
        playerInteractor.stop()
        playerInteractor.releasePlayer()
        stateLiveData.value = StateController.Default(currentFavoriteStatus)
    }
    fun onPause(){
        pausePlayer()
    }
    //endregion

    //region Favorite Button Functions
    fun onFavoriteButtonClicked() {
        when(currentFavoriteStatus) {
            true -> {
                deleteTrackFromFavorites()
                currentFavoriteStatus = false
                val currentState = stateLiveData.value
                currentState?.isFavorite = false
                stateLiveData.postValue(currentState!!)
            }
            else -> {
                makeTrackFavorite()
                currentFavoriteStatus = true
                val currentState = stateLiveData.value
                currentState?.isFavorite = true
                stateLiveData.postValue(currentState!!)

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
                val currentState = stateLiveData.value
                if(!tracks.first.isNullOrEmpty()) {
                    val isFavorite = tracks.first?.any { it.trackId == currentTrack.trackId }

                    currentTrack.isFavorite = isFavorite!!
                    currentFavoriteStatus = isFavorite

                    currentState?.isFavorite = isFavorite
                    stateLiveData.postValue(currentState!!)
                }else{
                    currentFavoriteStatus = false
                    currentState?.isFavorite = false
                    stateLiveData.postValue(currentState!!)
                }
            }
        }
    }
    //endregion
}