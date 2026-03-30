package com.la.weather.core.data.repository

import com.la.weather.core.common.di.IoDispatcher
import com.la.weather.core.data.mapper.toCity
import com.la.weather.core.domain.repository.CitySearchRepository
import com.la.weather.core.model.Resource
import com.la.weather.core.model.location.City
import com.la.weather.core.network.api.GeocodingApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CitySearchRepositoryImpl @Inject constructor(
    private val geocodingApi: GeocodingApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CitySearchRepository {

    override suspend fun searchCities(query: String): Resource<List<City>> =
        withContext(ioDispatcher) {
            try {
                val cities = geocodingApi.searchCity(query)
                    .results
                    ?.map { it.toCity() }
                    ?: emptyList()
                Resource.Success(cities)
            } catch (e: IOException) {
                Resource.Error(message = "No internet connection", throwable = e)
            } catch (e: HttpException) {
                Resource.Error(message = "Server error (${e.code()})", throwable = e)
            } catch (e: Exception) {
                Resource.Error(message = "Something went wrong", throwable = e)
            }
        }
}
