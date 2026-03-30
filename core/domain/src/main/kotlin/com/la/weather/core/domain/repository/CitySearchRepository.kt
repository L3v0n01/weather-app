package com.la.weather.core.domain.repository

import com.la.weather.core.model.Resource
import com.la.weather.core.model.location.City

interface CitySearchRepository {
    suspend fun searchCities(query: String): Resource<List<City>>
}
