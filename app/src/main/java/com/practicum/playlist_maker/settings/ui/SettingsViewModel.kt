package com.practicum.playlist_maker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlist_maker.settings.domain.api.SharingInteractor
import com.practicum.playlist_maker.settings.domain.model.EmailData
import com.practicum.playlist_maker.settings.domain.api.SettingsThemeModeInteractor

class SettingsViewModel(private val sharingInteractor: SharingInteractor,
                        private val settingsInteractor: SettingsThemeModeInteractor): ViewModel() {
    companion object {
        fun getFactory(sharingInteractor: SharingInteractor,
                       settingsInteractor: SettingsThemeModeInteractor): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor,settingsInteractor)
            }
        }
    }

    private val stateLiveData = MutableLiveData<Boolean>(getThemeState())
    fun observeSettingsState(): LiveData<Boolean> = stateLiveData


    fun getThemeState():Boolean{
        return settingsInteractor.getTheme()
    }

    fun saveThemeValue(isDarkTheme:Boolean){
        settingsInteractor.saveThemeModeValue(isDarkTheme)
        stateLiveData.postValue(isDarkTheme)
    }

    fun sharedLink(): String{
        return sharingInteractor.shareApp()
    }
    fun sendEmail(): EmailData{
        return sharingInteractor.openSupport()
    }
    fun openAgreement():String{
        return sharingInteractor.openTerms()
    }

}