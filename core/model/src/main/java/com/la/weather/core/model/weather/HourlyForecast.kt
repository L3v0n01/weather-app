package com.la.weather.core.model.weather

data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val weatherCondition: WeatherCondition,
    val precipitationProbability: Int,
    val windSpeed: Double,
)
