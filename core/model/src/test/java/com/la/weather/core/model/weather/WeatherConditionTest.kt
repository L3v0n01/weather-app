package com.la.weather.core.model.weather

import io.kotest.data.forAll
import io.kotest.data.row
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WeatherConditionTest {

    @Test
    fun `fromCode maps every defined WMO code to correct condition`() = runTest {
        forAll(
            row(0,  WeatherCondition.CLEAR_SKY),
            row(1,  WeatherCondition.MAINLY_CLEAR),
            row(2,  WeatherCondition.PARTLY_CLOUDY),
            row(3,  WeatherCondition.OVERCAST),
            row(45, WeatherCondition.FOG),
            row(48, WeatherCondition.DEPOSITING_RIME_FOG),
            row(51, WeatherCondition.DRIZZLE_LIGHT),
            row(53, WeatherCondition.DRIZZLE_MODERATE),
            row(55, WeatherCondition.DRIZZLE_DENSE),
            row(56, WeatherCondition.FREEZING_DRIZZLE_LIGHT),
            row(57, WeatherCondition.FREEZING_DRIZZLE_DENSE),
            row(61, WeatherCondition.RAIN_SLIGHT),
            row(63, WeatherCondition.RAIN_MODERATE),
            row(65, WeatherCondition.RAIN_HEAVY),
            row(66, WeatherCondition.FREEZING_RAIN_LIGHT),
            row(67, WeatherCondition.FREEZING_RAIN_HEAVY),
            row(71, WeatherCondition.SNOW_SLIGHT),
            row(73, WeatherCondition.SNOW_MODERATE),
            row(75, WeatherCondition.SNOW_HEAVY),
            row(77, WeatherCondition.SNOW_GRAINS),
            row(80, WeatherCondition.RAIN_SHOWERS_SLIGHT),
            row(81, WeatherCondition.RAIN_SHOWERS_MODERATE),
            row(82, WeatherCondition.RAIN_SHOWERS_VIOLENT),
            row(85, WeatherCondition.SNOW_SHOWERS_SLIGHT),
            row(86, WeatherCondition.SNOW_SHOWERS_HEAVY),
            row(95, WeatherCondition.THUNDERSTORM),
            row(96, WeatherCondition.THUNDERSTORM_HAIL_SLIGHT),
            row(99, WeatherCondition.THUNDERSTORM_HAIL_HEAVY),
        ) { code, expected ->
            assertEquals(expected, WeatherCondition.fromCode(code), "WMO code $code")
        }
    }

    @Test
    fun `fromCode returns UNKNOWN for unrecognised codes`() = runTest {
        forAll(
            row(-1),
            row(4),
            row(100),
            row(999),
        ) { code ->
            assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromCode(code))
        }
    }
}
