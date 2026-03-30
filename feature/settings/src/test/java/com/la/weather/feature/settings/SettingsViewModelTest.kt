package com.la.weather.feature.settings

import app.cash.turbine.test
import com.la.weather.core.datastore.WeatherPreferences
import com.la.weather.core.model.settings.TemperatureUnit
import com.la.weather.core.testing.MainDispatcherExtension
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class SettingsViewModelTest {

    private val weatherPreferences: WeatherPreferences = mockk()

    private fun createViewModel() = SettingsViewModel(weatherPreferences)

    @Test
    fun `initial state reflects celsius from preferences`() = runTest {
        every { weatherPreferences.temperatureUnit } returns flowOf(TemperatureUnit.CELSIUS)
        every { weatherPreferences.themeMode } returns flowOf(null)

        createViewModel().uiState.test {
            assertEquals(TemperatureUnit.CELSIUS, awaitItem().temperatureUnit)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial state reflects fahrenheit from preferences`() = runTest {
        every { weatherPreferences.temperatureUnit } returns flowOf(TemperatureUnit.FAHRENHEIT)
        every { weatherPreferences.themeMode } returns flowOf(null)

        createViewModel().uiState.test {
            assertEquals(TemperatureUnit.FAHRENHEIT, awaitItem().temperatureUnit)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `TemperatureUnitChanged persists new unit to preferences`() = runTest {
        every { weatherPreferences.temperatureUnit } returns flowOf(TemperatureUnit.CELSIUS)
        every { weatherPreferences.themeMode } returns flowOf(null)
        coEvery { weatherPreferences.setTemperatureUnit(any()) } just Runs

        createViewModel().handleEvent(SettingsUiEvent.TemperatureUnitChanged(TemperatureUnit.FAHRENHEIT))

        coVerify { weatherPreferences.setTemperatureUnit(TemperatureUnit.FAHRENHEIT) }
    }

    @Test
    fun `preference flow emission updates temperatureUnit state`() = runTest {
        val unitFlow = MutableStateFlow(TemperatureUnit.CELSIUS)
        every { weatherPreferences.temperatureUnit } returns unitFlow
        every { weatherPreferences.themeMode } returns flowOf(null)

        createViewModel().uiState.test {
            assertEquals(TemperatureUnit.CELSIUS, awaitItem().temperatureUnit)
            unitFlow.value = TemperatureUnit.FAHRENHEIT
            assertEquals(TemperatureUnit.FAHRENHEIT, awaitItem().temperatureUnit)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
