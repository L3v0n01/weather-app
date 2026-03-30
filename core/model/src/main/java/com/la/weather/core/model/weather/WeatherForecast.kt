package com.la.weather.core.model.weather

import kotlinx.collections.immutable.ImmutableList

data class WeatherForecast(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentWeather,
    val hourly: ImmutableList<HourlyForecast>,
    val daily: ImmutableList<DailyForecast>,
)
