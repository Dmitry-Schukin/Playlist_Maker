package com.practicum.playlist_maker.di

import com.practicum.playlist_maker.player.ui.AudioPlayerViewModel
import com.practicum.playlist_maker.search.ui.SearchViewModel
import com.practicum.playlist_maker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module{
    viewModel {
        SearchViewModel(
            androidContext(),
            get(),
            get())
    }
    viewModel { (url: String) ->
        AudioPlayerViewModel(url,get())
    }
    viewModel{
        SettingsViewModel(get(),get())
    }
}