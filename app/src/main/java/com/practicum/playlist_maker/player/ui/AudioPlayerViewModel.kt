package com.practicum.playlist_maker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel (urlPreview: String,
                            private val player: PlayerInteractor): ViewModel() {
    companion object {
        private const val UPDATE_DELAY = 200L
    }
    private var currentTrackUrl: String = urlPreview
    private val mediaPlayer = player.getPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var stateAndTime = StateController(MediaPlayerState.MEDIA_PLAYER_STATE_DEFAULT)

    private val stateAndTimerLiveData=
        MutableLiveData<StateController>(stateAndTime)
    fun observeStateAndTime(): LiveData<StateController> = stateAndTimerLiveData

    private var audioTimeRunnable= Runnable {
        if(stateAndTimerLiveData.value?.state==MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING){
            startTimerUpdate()
        }
    }


    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        postCurrentAudioTime(0)
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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(currentTrackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            stateAndTime = StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED)
            stateAndTimerLiveData.postValue(stateAndTime)
        }
        mediaPlayer.setOnCompletionListener {
            stateAndTime = StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED)
            stateAndTimerLiveData.postValue(stateAndTime)
            postCurrentAudioTime(0)
        }
    }

    fun startPlayer() {
        mediaPlayer.start()
        stateAndTime = StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING)
        stateAndTimerLiveData.postValue(stateAndTime)
        startTimerUpdate()
    }

    private fun startTimerUpdate() {
        stateAndTime.setTimerValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition))
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
        mediaPlayer.pause()
        stateAndTime = StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PAUSED)
        stateAndTime.setTimerValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition))
        stateAndTimerLiveData.postValue(stateAndTime)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(audioTimeRunnable)
    }
    fun onPause(){
        pausePlayer()
    }
}