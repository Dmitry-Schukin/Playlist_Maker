package com.practicum.playlist_maker.player.ui

import com.practicum.playlist_maker.library.domain.model.Playlist
import com.practicum.playlist_maker.player.domain.model.MediaPlayerState

sealed class StateController(
    val state: MediaPlayerState,
    val timer: String,
    var isFavorite: Boolean,
    var inPlaylist: AddingToPlaylistState,
    var playlists:List<Playlist>

) {
    class Default(
        favoriteState: Boolean,
        playlistState: AddingToPlaylistState,
        playlistsInDb:List<Playlist>
    ): StateController(
        MediaPlayerState.MEDIA_PLAYER_STATE_DEFAULT,
        "00:00",
        favoriteState,
        playlistState,
        playlistsInDb)

    class Prepared(
        favoriteState: Boolean,
        playlistState: AddingToPlaylistState,
        playlistsInDb:List<Playlist>
    ) : StateController(
        MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED,
        "00:00",
        favoriteState,
        playlistState,
        playlistsInDb)

    class Playing(
        timerValue: String,
        favoriteState: Boolean,
        playlistState: AddingToPlaylistState,
        playlistsInDb:List<Playlist>
    ) : StateController(
        MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING,
        timerValue,
        favoriteState,
        playlistState,
        playlistsInDb)

    class Paused(
        timerValue: String,
        favoriteState: Boolean,
        playlistState: AddingToPlaylistState,
        playlistsInDb:List<Playlist>
    ) : StateController(
        MediaPlayerState.MEDIA_PLAYER_STATE_PAUSED,
        timerValue,
        favoriteState,
        playlistState,
        playlistsInDb)


}