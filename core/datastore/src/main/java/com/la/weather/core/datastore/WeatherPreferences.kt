package com.la.weather.core.datastore

import com.la.weather.core.model.settings.TemperatureUnit
import com.la.weather.core.model.settings.ThemeMode
import kotlinx.coroutines.flow.Flow

interface WeatherPreferences {
    val temperatureUnit: Flow<TemperatureUnit>
    val lastLatitude: Flow<Double?>
    val lastLongitude: Flow<Double?>
    val lastCityName: Flow<String?>
    val themeMode: Flow<ThemeMode?>

    suspend fun setTemperatureUnit(unit: TemperatureUnit)
    suspend fun setLastLocation(latitude: Double, longitude: Double, cityName: String)
    suspend fun clearLastLocation()
    suspend fun setThemeMode(mode: ThemeMode)
}
