package com.practicum.playlist_maker.di

import com.practicum.playlist_maker.settings.data.impl.ExternalNavigatorImpl
import com.practicum.playlist_maker.settings.domain.api.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val navigatorModule = module{
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}