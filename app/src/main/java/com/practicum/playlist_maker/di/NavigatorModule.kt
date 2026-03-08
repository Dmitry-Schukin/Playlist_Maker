package com.practicum.playlist_maker.di

import com.practicum.playlist_maker.playlist_tracks.data.PlaylistSharingExternalNavigatorImpl
import com.practicum.playlist_maker.playlist_tracks.domain.PlaylistSharingExternalNavigator
import com.practicum.playlist_maker.settings.data.impl.ExternalNavigatorImpl
import com.practicum.playlist_maker.settings.domain.api.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val navigatorModule = module{
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
    single <PlaylistSharingExternalNavigator>{
        PlaylistSharingExternalNavigatorImpl(androidContext())
    }
}