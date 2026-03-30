package com.la.weather.core.data.repository

import com.la.weather.core.model.Resource
import com.la.weather.core.network.api.GeocodingApi
import com.la.weather.core.network.dto.GeocodingResponseDto
import com.la.weather.core.network.dto.GeocodingResultDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException

class CitySearchRepositoryImplTest {

    private val geocodingApi: GeocodingApi = mockk()
    private val repository = CitySearchRepositoryImpl(geocodingApi, UnconfinedTestDispatcher())

    @Test
    fun `searchCities returns mapped cities on success`() = runTest {
        coEvery { geocodingApi.searchCity(any(), any(), any()) } returns GeocodingResponseDto(listOf(fakeResult))

        val result = repository.searchCities("Paris") as Resource.Success

        assertEquals(1, result.data.size)
        assertEquals("Paris", result.data[0].name)
        assertEquals("FR", result.data[0].countryCode)
    }

    @Test
    fun `searchCities returns empty list when results null`() = runTest {
        coEvery { geocodingApi.searchCity(any(), any(), any()) } returns GeocodingResponseDto(null)

        assertEquals(Resource.Success(emptyList<Any>()), repository.searchCities("xyz"))
    }

    @Test
    fun `searchCities returns Error on network failure`() = runTest {
        coEvery { geocodingApi.searchCity(any(), any(), any()) } throws IOException("no network")

        assertTrue(repository.searchCities("Paris") is Resource.Error)
    }

    @Test
    fun `searchCities passes query to api`() = runTest {
        coEvery { geocodingApi.searchCity(any(), any(), any()) } returns GeocodingResponseDto()

        repository.searchCities("Berlin")

        coVerify { geocodingApi.searchCity(name = "Berlin", any(), any()) }
    }

    companion object {
        val fakeResult = GeocodingResultDto(
            id = 1L, name = "Paris", latitude = 48.8566, longitude = 2.3522,
            country = "France", countryCode = "FR", timezone = "Europe/Paris",
        )
    }
}
