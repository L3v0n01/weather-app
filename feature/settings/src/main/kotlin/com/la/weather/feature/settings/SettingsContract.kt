package com.la.weather.feature.settings

import com.la.weather.core.common.mvi.UiEffect
import com.la.weather.core.common.mvi.UiEvent
import com.la.weather.core.common.mvi.UiState
import com.la.weather.core.model.settings.TemperatureUnit
import com.la.weather.core.model.settings.ThemeMode

data class SettingsUiState(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.DEFAULT,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
) : UiState

sealed interface SettingsUiEvent : UiEvent {
    data class TemperatureUnitChanged(val unit: TemperatureUnit) : SettingsUiEvent
    data class ThemeModeChanged(val mode: ThemeMode) : SettingsUiEvent
}

sealed interface SettingsUiEffect : UiEffect
