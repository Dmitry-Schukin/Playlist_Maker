package com.practicum.playlist_maker.creator

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.practicum.playlist_maker.App.Companion.KEY_FOR_HISTORY
import com.practicum.playlist_maker.App.Companion.KEY_FOR_THEME
import com.practicum.playlist_maker.search.data.shared.HistoryPrefsStorageClient
import com.practicum.playlist_maker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlist_maker.settings.data.shared.SettingsPrefsStorageClient
import com.practicum.playlist_maker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlist_maker.search.data.network.TrackNetworkClient
import com.practicum.playlist_maker.search.data.network.TrackRepositoryImpl
import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.settings.domain.api.SettingsRepository
import com.practicum.playlist_maker.settings.domain.api.SettingsThemeModeInteractor
import com.practicum.playlist_maker.search.domain.api.TrackInteractor
import com.practicum.playlist_maker.search.domain.api.TrackRepository
import com.practicum.playlist_maker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlist_maker.settings.domain.impl.SettingsThemeModeInteractorImpl
import com.practicum.playlist_maker.search.domain.impl.TrackInteractorImpl


object Creator {
    //region Getting data from server
    private fun getTracksRepository(): TrackRepository {
        return TrackRepositoryImpl(TrackNetworkClient())
    }
    fun provideTracksInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTracksRepository())
    }
    //endregion

    //region Getting data from SharedPreferences for track history
    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            HistoryPrefsStorageClient<ArrayList<Track>>(
                context,
                KEY_FOR_HISTORY,
                object : TypeToken<ArrayList<Track>>() {}.type
            )
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }
    //endregion

    //region Getting data from SharedPreferences for theme selection
    private fun getSettingsThemeModeRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            SettingsPrefsStorageClient<Boolean>(
                context,
                KEY_FOR_THEME,
                object : TypeToken<Boolean>() {}.type
            )
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsThemeModeInteractor {
        return SettingsThemeModeInteractorImpl(getSettingsThemeModeRepository(context))
    }
    //endregion


}