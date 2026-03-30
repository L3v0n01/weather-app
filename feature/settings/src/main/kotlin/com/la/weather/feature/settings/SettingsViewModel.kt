package com.la.weather.feature.settings

import androidx.lifecycle.viewModelScope
import com.la.weather.core.common.mvi.BaseViewModel
import com.la.weather.core.datastore.WeatherPreferences
import com.la.weather.core.model.settings.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val weatherPreferences: WeatherPreferences,
) : BaseViewModel<SettingsUiState, SettingsUiEvent, SettingsUiEffect>() {

    override fun initialState() = SettingsUiState()

    init {
        observeTemperatureUnit()
        observeThemeMode()
    }

    private fun observeTemperatureUnit() {
        viewModelScope.launch {
            weatherPreferences.temperatureUnit.collect { unit ->
                updateState { copy(temperatureUnit = unit) }
            }
        }
    }

    private fun observeThemeMode() {
        viewModelScope.launch {
            weatherPreferences.themeMode.collect { mode ->
                updateState { copy(themeMode = mode ?: ThemeMode.SYSTEM) }
            }
        }
    }

    override fun handleEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.TemperatureUnitChanged -> {
                viewModelScope.launch {
                    weatherPreferences.setTemperatureUnit(event.unit)
                }
            }

            is SettingsUiEvent.ThemeModeChanged -> {
                viewModelScope.launch {
                    weatherPreferences.setThemeMode(event.mode)
                }
            }
        }
    }
}
