package com.la.weather.core.data.mapper

import com.la.weather.core.database.entity.WeatherCacheEntity
import com.la.weather.core.model.weather.CurrentWeather
import com.la.weather.core.model.weather.DailyForecast
import com.la.weather.core.model.weather.HourlyForecast
import com.la.weather.core.model.weather.WeatherCondition
import com.la.weather.core.model.weather.WeatherForecast
import com.la.weather.core.network.dto.WeatherResponseDto
import kotlinx.collections.immutable.toImmutableList

private const val DELIMITER = "|"

// DTO → Domain

fun WeatherResponseDto.toWeatherForecast(): WeatherForecast {
    val current = requireNotNull(current) { "Current weather is null" }
    val hourly = requireNotNull(hourly) { "Hourly forecast is null" }
    val daily = requireNotNull(daily) { "Daily forecast is null" }

    val hourlyForecasts = hourly.time.indices.map { i ->
        HourlyForecast(
            time = hourly.time[i],
            temperature = hourly.temperature[i],
            weatherCondition = WeatherCondition.fromCode(hourly.weatherCode[i]),
            precipitationProbability = hourly.precipitationProbability.getOrElse(i) { 0 },
            windSpeed = hourly.windSpeed[i],
        )
    }

    val dailyForecasts = daily.time.indices.map { i ->
        DailyForecast(
            date = daily.time[i],
            temperatureMax = daily.temperatureMax[i],
            temperatureMin = daily.temperatureMin[i],
            weatherCondition = WeatherCondition.fromCode(daily.weatherCode[i]),
            sunrise = daily.sunrise.getOrElse(i) { "" },
            sunset = daily.sunset.getOrElse(i) { "" },
            precipitationSum = daily.precipitationSum.getOrElse(i) { 0.0 },
            precipitationProbabilityMax = daily.precipitationProbabilityMax.getOrElse(i) { 0 },
        )
    }

    return WeatherForecast(
        latitude = latitude,
        longitude = longitude,
        timezone = timezone ?: "UTC",
        current = CurrentWeather(
            temperature = current.temperature,
            apparentTemperature = current.apparentTemperature,
            humidity = current.humidity,
            windSpeed = current.windSpeed,
            precipitation = current.precipitation,
            weatherCondition = WeatherCondition.fromCode(current.weatherCode),
            isDay = current.isDay == 1,
            time = current.time,
        ),
        hourly = hourlyForecasts.toImmutableList(),
        daily = dailyForecasts.toImmutableList(),
    )
}

// Domain → Entity

fun WeatherForecast.toEntity(locationKey: String): WeatherCacheEntity = WeatherCacheEntity(
    locationKey = locationKey,
    latitude = latitude,
    longitude = longitude,
    timezone = timezone,
    currentTemp = current.temperature,
    currentApparentTemp = current.apparentTemperature,
    currentHumidity = current.humidity,
    currentWindSpeed = current.windSpeed,
    currentPrecipitation = current.precipitation,
    currentWeatherCode = current.weatherCondition.code,
    currentIsDay = current.isDay,
    currentTime = current.time,
    hourlyTimes = hourly.joinToString(DELIMITER) { it.time },
    hourlyTemps = hourly.joinToString(DELIMITER) { it.temperature.toString() },
    hourlyWeatherCodes = hourly.joinToString(DELIMITER) { it.weatherCondition.code.toString() },
    hourlyPrecipProbs = hourly.joinToString(DELIMITER) { it.precipitationProbability.toString() },
    hourlyWindSpeeds = hourly.joinToString(DELIMITER) { it.windSpeed.toString() },
    dailyDates = daily.joinToString(DELIMITER) { it.date },
    dailyWeatherCodes = daily.joinToString(DELIMITER) { it.weatherCondition.code.toString() },
    dailyTempMaxes = daily.joinToString(DELIMITER) { it.temperatureMax.toString() },
    dailyTempMins = daily.joinToString(DELIMITER) { it.temperatureMin.toString() },
    dailySunrises = daily.joinToString(DELIMITER) { it.sunrise },
    dailySunsets = daily.joinToString(DELIMITER) { it.sunset },
    dailyPrecipSums = daily.joinToString(DELIMITER) { it.precipitationSum.toString() },
    dailyPrecipProbMaxes = daily.joinToString(DELIMITER) { it.precipitationProbabilityMax.toString() },
    cachedAt = System.currentTimeMillis(),
)

// Entity → Domain

fun WeatherCacheEntity.toWeatherForecast(): WeatherForecast {
    val times = hourlyTimes.splitList()
    val temps = hourlyTemps.splitDoubleList()
    val codes = hourlyWeatherCodes.splitIntList()
    val precipProbs = hourlyPrecipProbs.splitIntList()
    val windSpeeds = hourlyWindSpeeds.splitDoubleList()

    val hourlyForecasts = times.indices.map { i ->
        HourlyForecast(
            time = times[i],
            temperature = temps.getOrElse(i) { 0.0 },
            weatherCondition = WeatherCondition.fromCode(codes.getOrElse(i) { -1 }),
            precipitationProbability = precipProbs.getOrElse(i) { 0 },
            windSpeed = windSpeeds.getOrElse(i) { 0.0 },
        )
    }

    val dates = dailyDates.splitList()
    val dailyCodes = dailyWeatherCodes.splitIntList()
    val maxTemps = dailyTempMaxes.splitDoubleList()
    val minTemps = dailyTempMins.splitDoubleList()
    val sunrises = dailySunrises.splitList()
    val sunsets = dailySunsets.splitList()
    val precipSums = dailyPrecipSums.splitDoubleList()
    val precipProbMaxes = dailyPrecipProbMaxes.splitIntList()

    val dailyForecasts = dates.indices.map { i ->
        DailyForecast(
            date = dates[i],
            temperatureMax = maxTemps.getOrElse(i) { 0.0 },
            temperatureMin = minTemps.getOrElse(i) { 0.0 },
            weatherCondition = WeatherCondition.fromCode(dailyCodes.getOrElse(i) { -1 }),
            sunrise = sunrises.getOrElse(i) { "" },
            sunset = sunsets.getOrElse(i) { "" },
            precipitationSum = precipSums.getOrElse(i) { 0.0 },
            precipitationProbabilityMax = precipProbMaxes.getOrElse(i) { 0 },
        )
    }

    return WeatherForecast(
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        current = CurrentWeather(
            temperature = currentTemp,
            apparentTemperature = currentApparentTemp,
            humidity = currentHumidity,
            windSpeed = currentWindSpeed,
            precipitation = currentPrecipitation,
            weatherCondition = WeatherCondition.fromCode(currentWeatherCode),
            isDay = currentIsDay,
            time = currentTime,
        ),
        hourly = hourlyForecasts.toImmutableList(),
        daily = dailyForecasts.toImmutableList(),
    )
}

private fun String.splitList(): List<String> =
    if (isEmpty()) emptyList() else split(DELIMITER)

private fun String.splitDoubleList(): List<Double> =
    splitList().map { it.toDouble() }

private fun String.splitIntList(): List<Int> =
    splitList().map { it.toInt() }
