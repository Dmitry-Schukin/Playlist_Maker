package com.practicum.playlist_maker.common.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlist_maker.common.data.db.dao.TrackDao
import com.practicum.playlist_maker.common.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao

}