package com.practicum.playlist_maker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel (urlPreview: String,
                            private val playerInteractor: PlayerInteractor): ViewModel() {
    companion object {
        private const val UPDATE_DELAY = 300L
    }
    private var currentTrackUrl: String = urlPreview
    private var timerJob: Job? = null

    private val stateAndTimerLiveData=
        MutableLiveData<StateController>(StateController.Default())
    fun observeStateAndTime(): LiveData<StateController> = stateAndTimerLiveData


    init {
        playerInteractor.preparePlayer(currentTrackUrl,
            {
                stateAndTimerLiveData.postValue(StateController.Prepared())
            },
            {
                timerJob?.cancel()
                stateAndTimerLiveData.postValue(StateController.Prepared())
            })

    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
    }
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

}