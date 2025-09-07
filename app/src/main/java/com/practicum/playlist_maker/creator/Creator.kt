package com.practicum.playlist_maker.creator

import android.content.Context
import com.practicum.playlist_maker.data.HistorySharedPrefsManager
import com.practicum.playlist_maker.data.ThemeSharedPrefsManager
import com.practicum.playlist_maker.data.network.TrackNetworkClient
import com.practicum.playlist_maker.data.network.TrackRepositoryImpl
import com.practicum.playlist_maker.domain.api.TrackInteractor
import com.practicum.playlist_maker.domain.api.TrackRepository
import com.practicum.playlist_maker.domain.impl.TrackInteractorImpl
import com.practicum.playlist_maker.domain.use_case.HistorySharedPrefsUseCase
import com.practicum.playlist_maker.domain.use_case.ThemeSharedPrefsUseCase

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
    private fun getHistorySharedPrefsManager(context: Context): HistorySharedPrefsManager{
        return HistorySharedPrefsManager(context)
    }
    fun getHistorySharedPrefsUseCase(context: Context): HistorySharedPrefsUseCase {
        return HistorySharedPrefsUseCase(getHistorySharedPrefsManager(context))
    }
    //endregion

    //region Getting data from SharedPreferences for theme selection
    private fun getThemeSharedPrefsManager(context: Context): ThemeSharedPrefsManager {
        return ThemeSharedPrefsManager(context)
    }
    fun getThemeSharedPrefsUseCase(context: Context): ThemeSharedPrefsUseCase {
        return ThemeSharedPrefsUseCase(getThemeSharedPrefsManager(context))
    }
    //endregion
}