package com.practicum.playlist_maker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel (urlPreview: String): ViewModel() {
    companion object {
        fun getFactory(url: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(url)
            }
        }
        private const val UPDATE_DELAY = 200L
    }
    private var currentTrackUrl: String = urlPreview
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData =
        MutableLiveData<MediaPlayerState>(MediaPlayerState.MEDIA_PLAYER_STATE_DEFAULT)
    fun observePlayerState(): LiveData<MediaPlayerState> = stateLiveData

    private val progressTimeLiveData = MutableLiveData<String>("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private var audioTimeRunnable: kotlinx.coroutines.Runnable = Runnable {
        if (stateLiveData.value == MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING) {
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
        when(stateLiveData.value) {
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
            stateLiveData.postValue(MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            stateLiveData.postValue(MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED)
            postCurrentAudioTime(0)
        }
    }
    fun startPlayer() {
        mediaPlayer.start()
        stateLiveData.postValue(MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING)
        startTimerUpdate()
    }
    private fun startTimerUpdate() {
        progressTimeLiveData.postValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition))
        handler.postDelayed(audioTimeRunnable, UPDATE_DELAY)
    }

    private fun postCurrentAudioTime(currentTime: Long){
        val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val time = timeFormat.format(currentTime)
        progressTimeLiveData.postValue(time)
    }

    fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        stateLiveData.postValue(MediaPlayerState.MEDIA_PLAYER_STATE_PAUSED)
    }
    private fun pauseTimer() {
        handler.removeCallbacks(audioTimeRunnable)
    }
    fun onPause(){
        pausePlayer()
    }
}