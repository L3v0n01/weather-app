package com.la.weather.feature.home

import androidx.annotation.StringRes
import com.la.weather.core.common.mvi.UiEffect
import com.la.weather.core.common.mvi.UiEvent
import com.la.weather.core.common.mvi.UiState
import com.la.weather.core.model.settings.TemperatureUnit
import com.la.weather.core.model.location.City
import com.la.weather.core.model.weather.WeatherForecast

data class HomeUiState(
    val isLoading: Boolean = true,
    val forecast: WeatherForecast? = null,
    val cityName: String = "",
    val temperatureUnit: TemperatureUnit = TemperatureUnit.DEFAULT,
    @StringRes val errorResId: Int? = null,
    val errorMessage: String? = null,
) : UiState

sealed interface HomeUiEvent : UiEvent {
    data object Refresh : HomeUiEvent
    data class CitySelected(val city: City) : HomeUiEvent
}

sealed interface HomeUiEffect : UiEffect {
    data object RequestLocationPermission : HomeUiEffect
}
