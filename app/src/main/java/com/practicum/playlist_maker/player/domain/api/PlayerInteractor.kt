package com.practicum.playlist_maker.player.domain.api

import android.media.MediaPlayer

interface PlayerInteractor {
    fun getPlayer(): MediaPlayer
}