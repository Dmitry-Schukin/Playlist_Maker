package com.practicum.playlist_maker.player.domain.api

import android.media.MediaPlayer

interface PlayerRepository {
    fun getMediaPlayer(): MediaPlayer
}