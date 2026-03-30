package com.la.weather.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int = 0,
    val current: CurrentWeatherDto? = null,
    val hourly: HourlyWeatherDto? = null,
    val daily: DailyWeatherDto? = null,
)

@Serializable
data class CurrentWeatherDto(
    val time: String = "",
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("wind_speed_10m") val windSpeed: Double,
    @SerialName("apparent_temperature") val apparentTemperature: Double,
    @SerialName("relative_humidity_2m") val humidity: Int,
    val precipitation: Double,
    @SerialName("is_day") val isDay: Int, // 1 or 0
)

@Serializable
data class HourlyWeatherDto(
    val time: List<String>,
    @SerialName("temperature_2m") val temperature: List<Double>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("precipitation_probability") val precipitationProbability: List<Int>,
    @SerialName("wind_speed_10m") val windSpeed: List<Double>,
)

@Serializable
data class DailyWeatherDto(
    val time: List<String>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min") val temperatureMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerialName("precipitation_sum") val precipitationSum: List<Double>,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>,
)