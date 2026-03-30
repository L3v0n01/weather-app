package com.la.weather.core.model.weather

data class CurrentWeather(
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val precipitation: Double,
    val weatherCondition: WeatherCondition,
    val isDay: Boolean,
    val time: String,
)
