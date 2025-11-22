package com.practicum.playlist_maker.player.domain.impl

import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import com.practicum.playlist_maker.player.domain.api.PlayerRepository
import com.practicum.playlist_maker.player.domain.model.MediaPlayerState

class PlayerInteractorImpl(private val repository: PlayerRepository): PlayerInteractor {

    private var currentState = MediaPlayerState.MEDIA_PLAYER_STATE_DEFAULT

    override fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.preparePlayer(trackUrl,
            {
                currentState = MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED
                onPrepared()
            },
            {
                currentState = MediaPlayerState.MEDIA_PLAYER_STATE_PREPARED
                onCompletion()
            })
    }

    override fun startPlayer() {
        currentState= MediaPlayerState.MEDIA_PLAYER_STATE_PLAYING
        repository.startPlayer()
    }

    override fun pausePlayer() {
        currentState= MediaPlayerState.MEDIA_PLAYER_STATE_PAUSED
        repository.pausePlayer()
    }

    override fun getCurrentAudioTime(): Int {
        return repository.getCurrentAudioTime()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun getCurrentState(): MediaPlayerState {
        return currentState
    }

    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }

    override fun stop() {
        repository.stop()
    }
}