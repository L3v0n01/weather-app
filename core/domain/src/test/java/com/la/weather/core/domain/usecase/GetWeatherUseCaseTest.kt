package com.la.weather.core.domain.usecase

import com.la.weather.core.domain.repository.WeatherRepository
import com.la.weather.core.model.Resource
import com.la.weather.core.model.weather.CurrentWeather
import com.la.weather.core.model.weather.WeatherCondition
import com.la.weather.core.model.weather.WeatherForecast
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetWeatherUseCaseTest {

    private val repository: WeatherRepository = mockk()
    private val useCase = GetWeatherUseCase(repository)

    private val fakeForecast = WeatherForecast(
        latitude = 0.0, longitude = 0.0, timezone = "UTC",
        current = CurrentWeather(20.0, 18.0, 60, 5.0, 0.0, WeatherCondition.CLEAR_SKY, true, ""),
        hourly = persistentListOf(),
        daily = persistentListOf(),
    )

    @Test
    fun `invoke delegates to repository and returns result unchanged`() = runTest {
        val expected = Resource.Success(fakeForecast)
        coEvery { repository.getWeather(any(), any()) } returns expected

        assertEquals(expected, useCase(0.0, 0.0))
        coVerify(exactly = 1) { repository.getWeather(0.0, 0.0) }
    }

    @Test
    fun `invoke propagates repository errors`() = runTest {
        val expected = Resource.Error("timeout")
        coEvery { repository.getWeather(any(), any()) } returns expected

        assertEquals(expected, useCase(0.0, 0.0))
    }
}
