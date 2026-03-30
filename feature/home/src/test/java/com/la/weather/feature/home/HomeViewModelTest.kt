package com.la.weather.feature.home

import app.cash.turbine.test
import com.la.weather.core.datastore.WeatherPreferences
import com.la.weather.core.domain.usecase.GetWeatherUseCase
import com.la.weather.core.location.DeviceLocation
import com.la.weather.core.location.LocationError
import com.la.weather.core.location.LocationException
import com.la.weather.core.location.LocationProvider
import com.la.weather.core.model.Resource
import com.la.weather.core.model.location.City
import com.la.weather.core.model.settings.TemperatureUnit
import com.la.weather.core.model.weather.CurrentWeather
import com.la.weather.core.model.weather.WeatherCondition
import com.la.weather.core.model.weather.WeatherForecast
import com.la.weather.core.testing.MainDispatcherExtension
import kotlinx.collections.immutable.persistentListOf
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class HomeViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension(StandardTestDispatcher())

    private val getWeatherUseCase: GetWeatherUseCase = mockk()
    private val locationProvider: LocationProvider = mockk()
    private val weatherPreferences: WeatherPreferences = mockk()

    private val fakeWeatherForecast = WeatherForecast(
        latitude = 40.7128, longitude = -74.006, timezone = "UTC",
        current = CurrentWeather(20.0, 18.0, 60, 5.0, 0.0, WeatherCondition.CLEAR_SKY, true, ""),
        hourly = persistentListOf(), daily = persistentListOf(),
    )
    private val fakeCity = City(1L, "Paris", 48.8566, 2.3522, "France", "FR", null, "Europe/Paris", null)

    @BeforeEach
    fun setUp() {
        every { weatherPreferences.temperatureUnit } returns flowOf(TemperatureUnit.CELSIUS)
    }

    private fun createViewModel() =
        HomeViewModel(getWeatherUseCase, locationProvider, weatherPreferences)

    // ─── initial load ────────────────────────────────────────────────────────

    @Test
    fun `initial load with saved location emits loading then success`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            every { weatherPreferences.lastLatitude } returns flowOf(40.7128)
            every { weatherPreferences.lastLongitude } returns flowOf(-74.006)
            every { weatherPreferences.lastCityName } returns flowOf("New York")
            coEvery { getWeatherUseCase(40.7128, -74.006) } returns Resource.Success(fakeWeatherForecast)

            val vm = createViewModel()

            vm.uiState.test {
                assertTrue(awaitItem().isLoading)
                advanceUntilIdle()
                awaitItem().also { success ->
                    assertFalse(success.isLoading)
                    assertEquals(fakeWeatherForecast, success.forecast)
                    assertEquals("New York", success.cityName)
                    assertNull(success.errorResId)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `initial load shows error when weather API fails`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            every { weatherPreferences.lastLatitude } returns flowOf(40.7128)
            every { weatherPreferences.lastLongitude } returns flowOf(-74.006)
            every { weatherPreferences.lastCityName } returns flowOf("New York")
            coEvery { getWeatherUseCase(any(), any()) } returns Resource.Error("timeout")

            val vm = createViewModel()

            vm.uiState.test {
                awaitItem() // loading
                advanceUntilIdle()
                awaitItem().also { error ->
                    assertFalse(error.isLoading)
                    assertNotNull(error.errorMessage)
                    assertNull(error.forecast)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `initial load falls back to device location when no saved location`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            every { weatherPreferences.lastLatitude } returns flowOf(null)
            every { weatherPreferences.lastLongitude } returns flowOf(null)
            every { weatherPreferences.lastCityName } returns flowOf(null)
            coEvery { locationProvider.getCurrentLocation() } returns
                Result.success(DeviceLocation(48.8566, 2.3522))
            coEvery { getWeatherUseCase(48.8566, 2.3522) } returns Resource.Success(fakeWeatherForecast)

            val vm = createViewModel()

            vm.uiState.test {
                awaitItem() // loading
                advanceUntilIdle()
                assertFalse(awaitItem().isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `location permission denied emits RequestLocationPermission effect`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            every { weatherPreferences.lastLatitude } returns flowOf(null)
            every { weatherPreferences.lastLongitude } returns flowOf(null)
            every { weatherPreferences.lastCityName } returns flowOf(null)
            coEvery { locationProvider.getCurrentLocation() } returns
                Result.failure(LocationException(LocationError.PermissionDenied))

            val vm = createViewModel()

            vm.effects.test {
                advanceUntilIdle()
                assertEquals(HomeUiEffect.RequestLocationPermission, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ─── refresh ─────────────────────────────────────────────────────────────

    @Test
    fun `Refresh re-triggers weather load`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            every { weatherPreferences.lastLatitude } returns flowOf(40.7128)
            every { weatherPreferences.lastLongitude } returns flowOf(-74.006)
            every { weatherPreferences.lastCityName } returns flowOf("NYC")
            coEvery { getWeatherUseCase(any(), any()) } returns Resource.Success(fakeWeatherForecast)

            val vm = createViewModel()

            vm.uiState.test {
                awaitItem(); advanceUntilIdle(); awaitItem() // initial cycle

                vm.handleEvent(HomeUiEvent.Refresh)
                assertTrue(awaitItem().isLoading)
                advanceUntilIdle()
                assertFalse(awaitItem().isLoading)
                cancelAndIgnoreRemainingEvents()
            }

            coVerify(exactly = 2) { getWeatherUseCase(any(), any()) }
        }

    // ─── city selected ───────────────────────────────────────────────────────

    @Test
    fun `CitySelected saves location and loads weather for that city`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            every { weatherPreferences.lastLatitude } returns flowOf(null)
            every { weatherPreferences.lastLongitude } returns flowOf(null)
            every { weatherPreferences.lastCityName } returns flowOf(null)
            coEvery { locationProvider.getCurrentLocation() } returns
                Result.failure(LocationException(LocationError.GpsDisabled))
            coEvery { getWeatherUseCase(fakeCity.latitude, fakeCity.longitude) } returns
                Resource.Success(fakeWeatherForecast)
            coEvery { weatherPreferences.setLastLocation(any(), any(), any()) } just Runs

            val vm = createViewModel()

            vm.uiState.test {
                awaitItem(); advanceUntilIdle(); awaitItem() // initial cycle (error)

                vm.handleEvent(HomeUiEvent.CitySelected(fakeCity))
                awaitItem().also { loading ->
                    assertTrue(loading.isLoading)
                    assertEquals(fakeCity.name, loading.cityName)
                }
                advanceUntilIdle()
                awaitItem().also { success ->
                    assertFalse(success.isLoading)
                    assertEquals(fakeWeatherForecast, success.forecast)
                }
                cancelAndIgnoreRemainingEvents()
            }

            coVerify { weatherPreferences.setLastLocation(fakeCity.latitude, fakeCity.longitude, fakeCity.name) }
        }
}
