package com.practicum.playlist_maker.player.domain.api

import com.practicum.playlist_maker.player.domain.model.MediaPlayerState

interface PlayerInteractor {
    fun preparePlayer(trackUrl:String, onPrepared:()-> Unit, onCompletion:()->Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentAudioTime(): Int
    fun releasePlayer()
    fun getCurrentState(): MediaPlayerState
}