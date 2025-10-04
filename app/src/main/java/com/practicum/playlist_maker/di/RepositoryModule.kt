package com.practicum.playlist_maker.di

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
    single<SettingsRepository>{
        SettingsRepositoryImpl(get(named("settings")))
    }

}