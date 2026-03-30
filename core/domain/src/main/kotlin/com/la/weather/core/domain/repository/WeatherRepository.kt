package com.la.weather.core.domain.repository

import com.la.weather.core.model.Resource
import com.la.weather.core.model.weather.WeatherForecast

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): Resource<WeatherForecast>
}
