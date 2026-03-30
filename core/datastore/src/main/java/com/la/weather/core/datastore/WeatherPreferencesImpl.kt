package com.la.weather.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.la.weather.core.model.settings.TemperatureUnit
import com.la.weather.core.model.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherPreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : WeatherPreferences {

    private companion object {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val THEME_MODE = stringPreferencesKey("theme-mode")
        val LAST_LATITUDE = doublePreferencesKey("last_latitude")
        val LAST_LONGITUDE = doublePreferencesKey("last_longitude")
        val LAST_CITY_NAME = stringPreferencesKey("last_city_name")
    }

    override val temperatureUnit: Flow<TemperatureUnit> = dataStore.data.map { prefs ->
        TemperatureUnit.fromApiValue(prefs[TEMPERATURE_UNIT] ?: TemperatureUnit.DEFAULT.apiValue)
    }

    override val lastLatitude: Flow<Double?> = dataStore.data.map { prefs ->
        prefs[LAST_LATITUDE]
    }

    override val lastLongitude: Flow<Double?> = dataStore.data.map { prefs ->
        prefs[LAST_LONGITUDE]
    }

    override val lastCityName: Flow<String?> = dataStore.data.map { prefs ->
        prefs[LAST_CITY_NAME]
    }

    override val themeMode: Flow<ThemeMode?> = dataStore.data.map { prefs ->
        ThemeMode.fromApiValue(prefs[THEME_MODE] ?: ThemeMode.SYSTEM.apiValue)
    }

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        dataStore.edit { prefs -> prefs[TEMPERATURE_UNIT] = unit.apiValue }
    }

    override suspend fun setLastLocation(latitude: Double, longitude: Double, cityName: String) {
        dataStore.edit { prefs ->
            prefs[LAST_LATITUDE] = latitude
            prefs[LAST_LONGITUDE] = longitude
            prefs[LAST_CITY_NAME] = cityName
        }
    }

    override suspend fun clearLastLocation() {
        dataStore.edit { prefs ->
            prefs.remove(LAST_LATITUDE)
            prefs.remove(LAST_LONGITUDE)
            prefs.remove(LAST_CITY_NAME)
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs -> prefs[THEME_MODE] = mode.apiValue }
    }
}
