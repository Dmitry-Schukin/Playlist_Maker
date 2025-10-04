package com.practicum.playlist_maker.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlist_maker.common.data.NetworkClient
import com.practicum.playlist_maker.common.data.StorageClient
import com.practicum.playlist_maker.search.data.network.ItunesApiService
import com.practicum.playlist_maker.search.data.network.TrackNetworkClient
import com.practicum.playlist_maker.search.data.shared.HistoryPrefsStorageClient
import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.settings.data.shared.SettingsPrefsStorageClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val dataModule = module{

    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    single (named("history")) {
        androidContext()
            .getSharedPreferences("track_history", Context.MODE_PRIVATE)
    }

    single(named("settings")) {
        androidContext()
            .getSharedPreferences("light_dark_themes", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<StorageClient<ArrayList<Track>>>(named("history")){
        HistoryPrefsStorageClient(
            get(named("history")),
            get(),
            "key_for_track_parameters",
            object : TypeToken<ArrayList<Track>>() {}.type
        )
    }

    single<StorageClient<Boolean>> (named("settings")){
        SettingsPrefsStorageClient(
            get(named("settings")),
            get(),
            "key_for_light_dark_switch",
            object : TypeToken<Boolean>() {}.type
        )
    }
    single<NetworkClient>{
        TrackNetworkClient(get())
    }
}