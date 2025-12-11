package com.practicum.playlist_maker.player.ui

import com.practicum.playlist_maker.player.domain.model.MediaPlayerState

sealed class StateController(
    val state: MediaPlayerState,
    val timer: String,
    var isFavorite: Boolean
) {
    class Default(favoriteState: Boolean): StateController(MediaPlayerState.MEDIA_PLAYER_STATE_DEFAULT,"00:00", favoriteState)

    class Prepared(favoriteState: Boolean) : StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED,"00:00",favoriteState)

    class Playing(timerValue: String,favoriteState: Boolean) : StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING,timerValue,favoriteState)

    class Paused(timerValue: String,favoriteState: Boolean) : StateController(MediaPlayerState.MEDIA_PLAYER_STATE_PAUSED,timerValue,favoriteState)


}