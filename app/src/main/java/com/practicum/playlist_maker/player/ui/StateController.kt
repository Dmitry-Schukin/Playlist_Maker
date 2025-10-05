package com.practicum.playlist_maker.player.ui

import com.practicum.playlist_maker.player.domain.model.MediaPlayerState

data class StateController(
    val state: MediaPlayerState
) {
    private var timer: String = "00:00"

    fun setTimerValue(timerValue: String) {
        timer = timerValue
    }
    fun getTimerValue(): String {
        return timer
    }
}