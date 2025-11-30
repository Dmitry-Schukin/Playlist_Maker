package com.practicum.playlist_maker.player.domain.api

interface PlayerRepository {
    fun preparePlayer(trackUrl:String, onPrepared:()-> Unit, onCompletion:()->Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentAudioTime(): Int
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun stop()
}