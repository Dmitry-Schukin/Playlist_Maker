package com.practicum.playlist_maker.common.data

interface StorageClient<T> {
    fun save(objectForSaving:T)
    fun getSavedObject():T?
    fun clear()
}