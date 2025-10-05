package com.practicum.playlist_maker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlist_maker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl: PlayerRepository {
    override fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }
}