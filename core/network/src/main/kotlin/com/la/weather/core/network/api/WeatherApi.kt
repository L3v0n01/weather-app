package com.la.weather.core.network.api

import com.la.weather.core.network.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,weather_code,wind_speed_10m,apparent_temperature,relative_humidity_2m,precipitation,is_day",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,precipitation_probability,wind_speed_10m",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_sum,precipitation_probability_max",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("temperature_unit") temperatureUnit: String = "celsius",
        @Query("wind_speed_unit") windSpeedUnit: String = "kmh",
        @Query("precipitation_unit") precipitationUnit: String = "mm",
    ): WeatherResponseDto
}