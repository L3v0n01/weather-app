package com.la.weather.core.data.mapper

import com.la.weather.core.model.weather.CurrentWeather
import com.la.weather.core.model.weather.DailyForecast
import com.la.weather.core.model.weather.HourlyForecast
import com.la.weather.core.model.weather.WeatherCondition
import com.la.weather.core.network.dto.CurrentWeatherDto
import com.la.weather.core.network.dto.DailyWeatherDto
import com.la.weather.core.network.dto.HourlyWeatherDto
import com.la.weather.core.network.dto.WeatherResponseDto
import io.kotest.data.forAll
import io.kotest.data.row
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WeatherMapperTest {

    @Test
    fun `toWeatherForecast maps coordinates and timezone`() = runTest {
        forAll(
            row(48.8566,  2.3522,  "Europe/Paris"),
            row(40.7128, -74.006,  "America/New_York"),
            row(-33.865, 151.209, "Australia/Sydney"),
        ) { lat, lon, tz ->
            val forecast = buildDto(lat = lat, lon = lon, timezone = tz).toWeatherForecast()
            assertEquals(lat, forecast.latitude, 0.0001)
            assertEquals(lon, forecast.longitude, 0.0001)
            assertEquals(tz, forecast.timezone)
        }
    }

    @Test
    fun `toWeatherForecast maps current weather to correct CurrentWeather`() {
        val expected = CurrentWeather(
            temperature = 15.5, apparentTemperature = 13.0, humidity = 72,
            windSpeed = 20.0, precipitation = 1.2,
            weatherCondition = WeatherCondition.RAIN_SLIGHT, isDay = false,
            time = "2025-01-01T12:00",
        )
        val actual = buildDto(temp = 15.5, apparentTemp = 13.0, humidity = 72,
            windSpeed = 20.0, precipitation = 1.2, weatherCode = 61, isDay = 0)
            .toWeatherForecast().current
        assertEquals(expected, actual)
    }

    @Test
    fun `toWeatherForecast maps isDay int to boolean correctly`() = runTest {
        forAll(
            row(1, true),
            row(0, false),
        ) { isDayInt, expectedIsDay ->
            assertEquals(expectedIsDay, buildDto(isDay = isDayInt).toWeatherForecast().current.isDay)
        }
    }

    @Test
    fun `toWeatherForecast maps hourly forecast entries correctly`() {
        val dto = buildDto(
            hourlyTimes = listOf("2025-03-27T00:00", "2025-03-27T01:00"),
            hourlyTemps = listOf(12.0, 13.0),
            hourlyCodes = listOf(0, 1),
            hourlyPrecipProbs = listOf(0, 10),
            hourlyWindSpeeds = listOf(5.0, 6.0),
        )
        val hourly = dto.toWeatherForecast().hourly
        assertEquals(2, hourly.size)
        assertEquals(HourlyForecast("2025-03-27T00:00", 12.0, WeatherCondition.CLEAR_SKY, 0, 5.0), hourly[0])
        assertEquals(HourlyForecast("2025-03-27T01:00", 13.0, WeatherCondition.MAINLY_CLEAR, 10, 6.0), hourly[1])
    }

    @Test
    fun `toWeatherForecast maps daily forecast entries correctly`() {
        val dto = buildDto(
            dailyDates = listOf("2025-03-27", "2025-03-28"),
            dailyCodes = listOf(3, 63),
            dailyMaxTemps = listOf(18.0, 14.0),
            dailyMinTemps = listOf(10.0, 8.0),
        )
        val daily = dto.toWeatherForecast().daily
        assertEquals(2, daily.size)
        assertEquals(DailyForecast("2025-03-27", 18.0, 10.0, WeatherCondition.OVERCAST,
            "2025-03-27 T06:00", "2025-03-27 T20:00", 0.0, 0), daily[0])
        assertEquals(DailyForecast("2025-03-28", 14.0, 8.0, WeatherCondition.RAIN_MODERATE,
            "2025-03-28 T06:00", "2025-03-28 T20:00", 0.0, 0), daily[1])
    }

    @Test
    fun `entity round-trip restores identical WeatherForecast`() {
        val original = buildDto(
            temp = 25.0, apparentTemp = 23.0, humidity = 50,
            windSpeed = 15.0, precipitation = 0.5, weatherCode = 2, isDay = 1,
            hourlyTimes = listOf("T00", "T01", "T02"),
            hourlyTemps = listOf(10.0, 11.0, 12.0),
            hourlyCodes = listOf(0, 0, 1),
            hourlyPrecipProbs = listOf(0, 0, 5),
            hourlyWindSpeeds = listOf(4.0, 5.0, 6.0),
            dailyDates = listOf("D1", "D2"),
            dailyCodes = listOf(0, 3),
            dailyMaxTemps = listOf(20.0, 18.0),
            dailyMinTemps = listOf(12.0, 10.0),
        ).toWeatherForecast()

        assertEquals(original, original.toEntity("key").toWeatherForecast())
    }

    // ─── builder ─────────────────────────────────────────────────────────────

    private fun buildDto(
        lat: Double = 0.0, lon: Double = 0.0, timezone: String = "UTC",
        temp: Double = 20.0, apparentTemp: Double = 18.0, humidity: Int = 60,
        windSpeed: Double = 5.0, precipitation: Double = 0.0,
        weatherCode: Int = 0, isDay: Int = 1,
        hourlyTimes: List<String> = listOf("2025-01-01T00:00"),
        hourlyTemps: List<Double> = listOf(20.0),
        hourlyCodes: List<Int> = listOf(0),
        hourlyPrecipProbs: List<Int> = listOf(0),
        hourlyWindSpeeds: List<Double> = listOf(5.0),
        dailyDates: List<String> = listOf("2025-01-01"),
        dailyCodes: List<Int> = listOf(0),
        dailyMaxTemps: List<Double> = listOf(22.0),
        dailyMinTemps: List<Double> = listOf(14.0),
    ) = WeatherResponseDto(
        latitude = lat, longitude = lon, timezone = timezone,
        current = CurrentWeatherDto(
            time = "2025-01-01T12:00", temperature = temp, weatherCode = weatherCode,
            windSpeed = windSpeed, apparentTemperature = apparentTemp,
            humidity = humidity, precipitation = precipitation, isDay = isDay,
        ),
        hourly = HourlyWeatherDto(
            time = hourlyTimes, temperature = hourlyTemps, weatherCode = hourlyCodes,
            precipitationProbability = hourlyPrecipProbs, windSpeed = hourlyWindSpeeds,
        ),
        daily = DailyWeatherDto(
            time = dailyDates, weatherCode = dailyCodes,
            temperatureMax = dailyMaxTemps, temperatureMin = dailyMinTemps,
            sunrise = dailyDates.map { "$it T06:00" }, sunset = dailyDates.map { "$it T20:00" },
            precipitationSum = dailyDates.map { 0.0 }, precipitationProbabilityMax = dailyDates.map { 0 },
        ),
    )
}
