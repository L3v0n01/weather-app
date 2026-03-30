package com.la.weather.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.la.weather.core.database.dao.WeatherDao
import com.la.weather.core.database.entity.WeatherCacheEntity

@Database(
    entities = [WeatherCacheEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
