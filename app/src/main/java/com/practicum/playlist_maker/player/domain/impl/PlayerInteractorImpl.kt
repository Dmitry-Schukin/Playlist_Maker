package com.practicum.playlist_maker.player.domain.impl

import android.media.MediaPlayer
import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import com.practicum.playlist_maker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository): PlayerInteractor {
    override fun getPlayer(): MediaPlayer {
        return repository.getMediaPlayer()
    }
}