package com.la.weather.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached weather forecast keyed by rounded lat/lon.
 * Lists are stored as pipe-delimited strings to avoid extra serialization dependencies.
 */
@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val locationKey: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    // Current weather
    val currentTemp: Double,
    val currentApparentTemp: Double,
    val currentHumidity: Int,
    val currentWindSpeed: Double,
    val currentPrecipitation: Double,
    val currentWeatherCode: Int,
    val currentIsDay: Boolean,
    val currentTime: String,
    // Hourly — parallel pipe-delimited lists
    val hourlyTimes: String,
    val hourlyTemps: String,
    val hourlyWeatherCodes: String,
    val hourlyPrecipProbs: String,
    val hourlyWindSpeeds: String,
    // Daily — parallel pipe-delimited lists
    val dailyDates: String,
    val dailyWeatherCodes: String,
    val dailyTempMaxes: String,
    val dailyTempMins: String,
    val dailySunrises: String,
    val dailySunsets: String,
    val dailyPrecipSums: String,
    val dailyPrecipProbMaxes: String,
    val cachedAt: Long,
)
