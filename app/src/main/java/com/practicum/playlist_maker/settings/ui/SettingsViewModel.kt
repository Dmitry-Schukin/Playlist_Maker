package com.practicum.playlist_maker.settings.ui

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlist_maker.App
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.creator.Creator

class SettingsViewModel(private val context: Context): ViewModel() {
    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as App)
                SettingsViewModel(app)
            }
        }
    }
    private val settingsInteractor = Creator.provideSettingsInteractor(context)
    private val stateLiveData = MutableLiveData<Boolean>(getThemeState())
    fun observeSettingsState(): LiveData<Boolean> = stateLiveData

    private val sharedLiveData = SingleLiveEvent<String>()
    fun observeShared(): LiveData<String> = sharedLiveData

    private val emailLiveData = SingleLiveEvent<String>()
    fun observeEmail(): LiveData<String> = emailLiveData

    private val agreementLiveData = SingleLiveEvent<String>()
    fun observeAgreement(): LiveData<String> = agreementLiveData

    fun getThemeState():Boolean{
        return settingsInteractor.getTheme()
    }

    fun saveThemeValue(isDarkTheme:Boolean){
        settingsInteractor.saveThemeModeValue(isDarkTheme)
        stateLiveData.postValue(isDarkTheme)
    }

    fun sharedLink(){
        sharedLiveData.postValue(ContextCompat.getString(context, R.string.url_practicum_course))
    }
    fun sendEmail(){
        emailLiveData.postValue(ContextCompat.getString(context, R.string.my_mail))
    }
    fun openAgreement(){
        agreementLiveData.postValue(ContextCompat.getString(context, R.string.url_practicum_offer))
    }

}