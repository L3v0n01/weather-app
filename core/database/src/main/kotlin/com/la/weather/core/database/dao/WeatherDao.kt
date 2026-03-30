package com.la.weather.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.la.weather.core.database.entity.WeatherCacheEntity

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey")
    suspend fun getWeatherCache(locationKey: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCache(entity: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE locationKey = :locationKey")
    suspend fun deleteWeatherCache(locationKey: String): Int

    @Query("DELETE FROM weather_cache WHERE cachedAt < :threshold")
    suspend fun deleteStaleCache(threshold: Long): Int
}
