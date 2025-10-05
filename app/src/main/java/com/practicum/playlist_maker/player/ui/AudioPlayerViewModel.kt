package com.practicum.playlist_maker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import com.practicum.playlist_maker.player.domain.model.MediaPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel (urlPreview: String,
                            private val playerInteractor: PlayerInteractor): ViewModel() {
    companion object {
        private const val UPDATE_DELAY = 200L
    }
    private var currentTrackUrl: String = urlPreview
    private val handler = Handler(Looper.getMainLooper())
    private var stateAndTime = StateController(playerInteractor.getCurrentState())

    private val stateAndTimerLiveData=
        MutableLiveData<StateController>(stateAndTime)
    fun observeStateAndTime(): LiveData<StateController> = stateAndTimerLiveData

    private var audioTimeRunnable= Runnable {
        if(stateAndTimerLiveData.value?.state== MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING){
            startTimerUpdate()
        }
    }

    init {
        playerInteractor.preparePlayer(currentTrackUrl,
            {
                stateAndTime = StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED)
                stateAndTimerLiveData.postValue(stateAndTime)
            },
            {
                stateAndTime = StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED)
                postCurrentAudioTime(0)
            })

    }

    override fun onCleared() {
        super.onCleared()
        postCurrentAudioTime(0)
        playerInteractor.releasePlayer()

    }
    fun onPlayButtonClicked() {
        when(stateAndTimerLiveData.value?.state) {
            MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING -> {
                pausePlayer()
            }
            MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED, MediaPlayerState.MEDIA_PLAYER_STATE_PAUSED -> {
                startPlayer()
            }
            else -> {}
        }
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        stateAndTime = StateController(playerInteractor.getCurrentState())
        stateAndTimerLiveData.postValue(stateAndTime)
        startTimerUpdate()
    }

    private fun startTimerUpdate() {
        stateAndTime.setTimerValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentAudioTime()))
        stateAndTimerLiveData.postValue(stateAndTime)
        handler.postDelayed(audioTimeRunnable, UPDATE_DELAY)
    }


    private fun postCurrentAudioTime(currentTime: Long){
        val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val time = timeFormat.format(currentTime)
        stateAndTime.setTimerValue(time)
        stateAndTimerLiveData.postValue(stateAndTime)
    }

    fun pausePlayer() {
        pauseTimer()
        playerInteractor.pausePlayer()
        stateAndTime = StateController(playerInteractor.getCurrentState())
        stateAndTime.setTimerValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentAudioTime()))
        stateAndTimerLiveData.postValue(stateAndTime)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(audioTimeRunnable)
    }
    fun onPause(){
        pausePlayer()
    }
}