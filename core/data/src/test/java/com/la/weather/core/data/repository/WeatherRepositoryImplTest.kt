package com.la.weather.core.data.repository

import com.la.weather.core.data.mapper.toEntity
import com.la.weather.core.data.mapper.toWeatherForecast
import com.la.weather.core.database.dao.WeatherDao
import com.la.weather.core.model.Resource
import com.la.weather.core.network.api.WeatherApi
import com.la.weather.core.network.dto.CurrentWeatherDto
import com.la.weather.core.network.dto.DailyWeatherDto
import com.la.weather.core.network.dto.HourlyWeatherDto
import com.la.weather.core.network.dto.WeatherResponseDto
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException

class WeatherRepositoryImplTest {

    private val weatherApi: WeatherApi = mockk()
    private val weatherDao: WeatherDao = mockk()
    private val repository = WeatherRepositoryImpl(weatherApi, weatherDao, UnconfinedTestDispatcher())

    @Test
    fun `getWeather returns Success and caches when API succeeds`() = runTest {
        coEvery { weatherApi.getWeather(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns fakeDto
        coEvery { weatherDao.insertWeatherCache(any()) } just Runs

        val result = repository.getWeather(LAT, LON)

        assertTrue(result is Resource.Success)
        coVerify { weatherDao.insertWeatherCache(any()) }
    }

    @Test
    fun `getWeather returns cached data when API throws IOException`() = runTest {
        val cached = fakeDto.toWeatherForecast().toEntity("${LAT}_${LON}")
        coEvery { weatherApi.getWeather(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } throws IOException("no network")
        coEvery { weatherDao.getWeatherCache(any()) } returns cached

        val result = repository.getWeather(LAT, LON)

        assertTrue(result is Resource.Success)
        assertEquals(cached.toWeatherForecast(), (result as Resource.Success).data)
    }

    @Test
    fun `getWeather returns Error when API fails and no cache`() = runTest {
        coEvery { weatherApi.getWeather(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } throws IOException("no network")
        coEvery { weatherDao.getWeatherCache(any()) } returns null

        assertTrue(repository.getWeather(LAT, LON) is Resource.Error)
    }

    @Test
    fun `getWeather error message is user-friendly for IOException`() = runTest {
        coEvery { weatherApi.getWeather(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } throws IOException("connection refused")
        coEvery { weatherDao.getWeatherCache(any()) } returns null

        val result = repository.getWeather(LAT, LON) as Resource.Error
        assertEquals("No internet connection", result.message)
        assertTrue(result.throwable is IOException)
    }

    companion object {
        private const val LAT = 40.7128
        private const val LON = -74.006

        val fakeDto = WeatherResponseDto(
            latitude = LAT, longitude = LON, timezone = "UTC",
            current = CurrentWeatherDto(
                time = "2025-03-27T12:00", temperature = 20.0, weatherCode = 0,
                windSpeed = 5.0, apparentTemperature = 18.0, humidity = 60,
                precipitation = 0.0, isDay = 1,
            ),
            hourly = HourlyWeatherDto(
                time = listOf("2025-03-27T00:00"), temperature = listOf(20.0),
                weatherCode = listOf(0), precipitationProbability = listOf(0), windSpeed = listOf(5.0),
            ),
            daily = DailyWeatherDto(
                time = listOf("2025-03-27"), weatherCode = listOf(0),
                temperatureMax = listOf(22.0), temperatureMin = listOf(14.0),
                sunrise = listOf("2025-03-27T06:30"), sunset = listOf("2025-03-27T20:00"),
                precipitationSum = listOf(0.0), precipitationProbabilityMax = listOf(0),
            ),
        )
    }
}
