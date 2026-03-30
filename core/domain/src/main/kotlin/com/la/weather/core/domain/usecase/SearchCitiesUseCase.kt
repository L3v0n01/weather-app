package com.la.weather.core.domain.usecase

import com.la.weather.core.domain.repository.CitySearchRepository
import com.la.weather.core.model.Resource
import com.la.weather.core.model.location.City
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    private val citySearchRepository: CitySearchRepository,
) {
    suspend operator fun invoke(query: String): Resource<List<City>> =
        citySearchRepository.searchCities(query)
}
