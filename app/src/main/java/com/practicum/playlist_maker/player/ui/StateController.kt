package com.practicum.playlist_maker.player.ui

import com.practicum.playlist_maker.player.domain.model.MediaPlayerState

sealed class StateController(
    val state: MediaPlayerState,
    val timer: String
) {
    class Default : StateController(MediaPlayerState.MEDIA_PLAYER_STATE_DEFAULT,"00:00")

    class Prepared : StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED,"00:00")

    class Playing(timerValue: String) : StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING,timerValue)

    class Paused(timerValue: String) : StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PAUSED,timerValue)

}