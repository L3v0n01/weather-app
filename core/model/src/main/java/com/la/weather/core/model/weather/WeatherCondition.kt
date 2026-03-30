package com.la.weather.core.model.weather

enum class WeatherCondition(val code: Int, val description: String) {
    CLEAR_SKY(0, "Clear sky"),
    MAINLY_CLEAR(1, "Mainly clear"),
    PARTLY_CLOUDY(2, "Partly cloudy"),
    OVERCAST(3, "Overcast"),
    FOG(45, "Fog"),
    DEPOSITING_RIME_FOG(48, "Depositing rime fog"),
    DRIZZLE_LIGHT(51, "Light drizzle"),
    DRIZZLE_MODERATE(53, "Moderate drizzle"),
    DRIZZLE_DENSE(55, "Dense drizzle"),
    FREEZING_DRIZZLE_LIGHT(56, "Light freezing drizzle"),
    FREEZING_DRIZZLE_DENSE(57, "Dense freezing drizzle"),
    RAIN_SLIGHT(61, "Slight rain"),
    RAIN_MODERATE(63, "Moderate rain"),
    RAIN_HEAVY(65, "Heavy rain"),
    FREEZING_RAIN_LIGHT(66, "Light freezing rain"),
    FREEZING_RAIN_HEAVY(67, "Heavy freezing rain"),
    SNOW_SLIGHT(71, "Slight snowfall"),
    SNOW_MODERATE(73, "Moderate snowfall"),
    SNOW_HEAVY(75, "Heavy snowfall"),
    SNOW_GRAINS(77, "Snow grains"),
    RAIN_SHOWERS_SLIGHT(80, "Slight rain showers"),
    RAIN_SHOWERS_MODERATE(81, "Moderate rain showers"),
    RAIN_SHOWERS_VIOLENT(82, "Violent rain showers"),
    SNOW_SHOWERS_SLIGHT(85, "Slight snow showers"),
    SNOW_SHOWERS_HEAVY(86, "Heavy snow showers"),
    THUNDERSTORM(95, "Thunderstorm"),
    THUNDERSTORM_HAIL_SLIGHT(96, "Thunderstorm with slight hail"),
    THUNDERSTORM_HAIL_HEAVY(99, "Thunderstorm with heavy hail"),
    UNKNOWN(-1, "Unknown");

    companion object {
        fun fromCode(code: Int): WeatherCondition =
            entries.find { it.code == code } ?: UNKNOWN
    }
}
