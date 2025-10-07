package com.practicum.playlist_maker.di

import android.media.MediaPlayer
import com.practicum.playlist_maker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlist_maker.player.domain.api.PlayerRepository
import com.practicum.playlist_maker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlist_maker.search.data.network.TrackRepositoryImpl
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.search.domain.api.TrackRepository
import com.practicum.playlist_maker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlist_maker.settings.domain.api.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module{
    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }
    single <SearchHistoryRepository>{
        SearchHistoryRepositoryImpl(get(named("history")))
    }
    factory<SettingsRepository>{
        SettingsRepositoryImpl(get(named("settings")))
    }

    factory{ MediaPlayer() }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }
}