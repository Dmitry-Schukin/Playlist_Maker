package com.practicum.playlist_maker.domain.use_case

import com.practicum.playlist_maker.data.ThemeSharedPrefsManager


class ThemeSharedPrefsUseCase(
    private val themeManager: ThemeSharedPrefsManager
) {
    fun executeSavingThemeState(state: Boolean){
        themeManager.save(state)
    }
    fun executeGettingThemeState(default:Boolean):Boolean{
        return themeManager.getSavedObjectWithDefaultParam(default)
    }
    fun clear(){
        themeManager.clear()
    }
}
