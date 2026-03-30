package com.la.weather.core.model.weather

data class DailyForecast(
    val date: String,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val weatherCondition: WeatherCondition,
    val sunrise: String,
    val sunset: String,
    val precipitationSum: Double,
    val precipitationProbabilityMax: Int,
)
