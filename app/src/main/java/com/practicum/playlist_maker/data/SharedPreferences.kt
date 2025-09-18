package com.practicum.playlist_maker.data

interface SharedPreferences<T> {
    fun save(objectForSaving:T)
    fun getSavedObject():T
    fun clear()
}