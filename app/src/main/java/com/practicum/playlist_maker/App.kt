package com.practicum.playlist_maker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlist_maker.di.dataModule
import com.practicum.playlist_maker.di.interactorModule
import com.practicum.playlist_maker.di.navigatorModule
import com.practicum.playlist_maker.di.repositoryModule
import com.practicum.playlist_maker.di.viewModelModule
import com.practicum.playlist_maker.settings.domain.api.SettingsThemeModeInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, navigatorModule, repositoryModule, interactorModule, viewModelModule)
        }
        val settingsInteractor = getKoin().get<SettingsThemeModeInteractor>()
        val darkTheme = settingsInteractor.getTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        settingsInteractor.saveThemeModeValue(darkTheme)
    }


}

