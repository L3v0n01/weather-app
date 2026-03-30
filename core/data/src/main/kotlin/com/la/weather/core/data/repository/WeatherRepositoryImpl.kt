package com.la.weather.core.data.repository

import com.la.weather.core.common.di.IoDispatcher
import com.la.weather.core.data.mapper.toEntity
import com.la.weather.core.data.mapper.toWeatherForecast
import com.la.weather.core.database.dao.WeatherDao
import com.la.weather.core.domain.repository.WeatherRepository
import com.la.weather.core.model.Resource
import com.la.weather.core.model.weather.WeatherForecast
import com.la.weather.core.network.api.WeatherApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : WeatherRepository {

    override suspend fun getWeather(latitude: Double, longitude: Double): Resource<WeatherForecast> =
        withContext(ioDispatcher) {
            val locationKey = "${latitude}_${longitude}"
            try {
                val forecast = weatherApi.getWeather(latitude, longitude).toWeatherForecast()
                weatherDao.insertWeatherCache(forecast.toEntity(locationKey))
                Resource.Success(forecast)
            } catch (e: IOException) {
                val cached = weatherDao.getWeatherCache(locationKey)
                if (cached != null) {
                    Resource.Success(cached.toWeatherForecast())
                } else {
                    Resource.Error(message = "No internet connection", throwable = e)
                }
            } catch (e: HttpException) {
                Resource.Error(message = "Server error (${e.code()})", throwable = e)
            } catch (e: Exception) {
                Resource.Error(message = "Something went wrong", throwable = e)
            }
        }

}
