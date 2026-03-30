package com.la.weather.core.domain.usecase

import com.la.weather.core.domain.repository.CitySearchRepository
import com.la.weather.core.model.Resource
import com.la.weather.core.model.location.City
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SearchCitiesUseCaseTest {

    private val repository: CitySearchRepository = mockk()
    private val useCase = SearchCitiesUseCase(repository)

    private val fakeCity = City(1L, "Paris", 0.0, 0.0, "", "", null, "UTC", null)

    @Test
    fun `invoke delegates to repository and returns result unchanged`() = runTest {
        val expected = Resource.Success(listOf(fakeCity))
        coEvery { repository.searchCities("Paris") } returns expected

        assertEquals(expected, useCase("Paris"))
        coVerify(exactly = 1) { repository.searchCities("Paris") }
    }

    @Test
    fun `invoke returns empty list when no cities found`() = runTest {
        coEvery { repository.searchCities(any()) } returns Resource.Success(emptyList())

        assertEquals(Resource.Success(emptyList<City>()), useCase("xyz"))
    }
}
