package com.practicum.playlist_maker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlist_maker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer): PlayerRepository {

    override fun preparePlayer(trackUrl:String, onPrepared:()-> Unit, onCompletion:()->Unit) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared()
        }
        mediaPlayer.setOnCompletionListener{
            onCompletion()
        }
    }
    override fun startPlayer(){
        mediaPlayer.start()
    }
    override fun pausePlayer() {
        mediaPlayer.pause()
    }
    override fun getCurrentAudioTime():Int{
        return mediaPlayer.currentPosition
    }
    override fun releasePlayer() {
        mediaPlayer.release()
    }
    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
    override fun stop() {
        mediaPlayer.stop()
    }
}