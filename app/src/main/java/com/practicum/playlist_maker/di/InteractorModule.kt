package com.practicum.playlist_maker.di

import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.TrackInteractor
import com.practicum.playlist_maker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlist_maker.search.domain.impl.TrackInteractorImpl
import com.practicum.playlist_maker.settings.domain.api.SettingsThemeModeInteractor
import com.practicum.playlist_maker.settings.domain.api.SharingInteractor
import com.practicum.playlist_maker.settings.domain.impl.SettingsThemeModeInteractorImpl
import com.practicum.playlist_maker.settings.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module{
    factory<TrackInteractor> {
        TrackInteractorImpl(get())
    }
    factory<SearchHistoryInteractor>{
        SearchHistoryInteractorImpl(get())
    }
    factory<SettingsThemeModeInteractor>{
        SettingsThemeModeInteractorImpl(get())
    }
    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }
}