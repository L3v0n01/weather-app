package com.la.weather.core.domain.usecase

import com.la.weather.core.domain.repository.WeatherRepository
import com.la.weather.core.model.Resource
import com.la.weather.core.model.weather.WeatherForecast
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Resource<WeatherForecast> =
        weatherRepository.getWeather(latitude, longitude)
}
