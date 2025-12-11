package com.practicum.playlist_maker.di

import android.media.MediaPlayer
import com.practicum.playlist_maker.common.data.db.converters.TrackDbConvertor
import com.practicum.playlist_maker.library.data.impl.FavoritesDBRepositoryImpl
import com.practicum.playlist_maker.library.domain.api.FavoritesDBRepository
import com.practicum.playlist_maker.library.data.impl.PlaylistRepositoryImpl
import com.practicum.playlist_maker.library.domain.api.PlaylistRepository
import com.practicum.playlist_maker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlist_maker.player.domain.api.PlayerRepository
import com.practicum.playlist_maker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlist_maker.search.data.impl.TrackRepositoryImpl
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.search.domain.api.TrackRepository
import com.practicum.playlist_maker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlist_maker.settings.domain.api.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module{
    single<TrackRepository> {
        TrackRepositoryImpl(get(),get())
    }
    single <SearchHistoryRepository>{
        SearchHistoryRepositoryImpl(get(named("history")),get())
    }
    factory<SettingsRepository>{
        SettingsRepositoryImpl(get(named("settings")))
    }

    factory{ MediaPlayer() }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    factory<PlaylistRepository> {
        PlaylistRepositoryImpl()
    }
    factory {
        TrackDbConvertor()
    }
    single<FavoritesDBRepository> {
        FavoritesDBRepositoryImpl(get(),get())
    }
}