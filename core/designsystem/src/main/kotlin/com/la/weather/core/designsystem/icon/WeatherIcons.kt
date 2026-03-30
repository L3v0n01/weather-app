package com.la.weather.core.designsystem.icon

object WeatherIcons {

    private const val BASE = "weather_icons"

    fun fromWmoCode(code: Int, isDay: Boolean, isDarkTheme: Boolean): String {
        val name = resolveIconName(code, isDay)
        val suffix = if (isDarkTheme) "_dark" else ""
        return "$BASE/${name}${suffix}.svg"
    }

    private fun resolveIconName(code: Int, isDay: Boolean): String = when (code) {
        // Clear sky
        0 -> if (isDay) "sunny" else "clear"
        // Mainly clear
        1 -> if (isDay) "mostly_sunny" else "mostly_clear"
        // Partly cloudy
        2 -> if (isDay) "partly_cloudy" else "partly_clear"
        // Overcast
        3 -> "cloudy"
        // Fog / depositing rime fog
        45, 48 -> "cloudy"
        // Drizzle (light, moderate, dense)
        51, 53, 55 -> "drizzle"
        // Freezing drizzle
        56, 57 -> "wintry_mix"
        // Rain (slight, moderate)
        61, 63 -> "showers"
        // Rain (heavy)
        65 -> "heavy"
        // Freezing rain
        66, 67 -> "wintry_mix"
        // Snowfall (slight, moderate)
        71, 73 -> "snow_showers"
        // Snowfall (heavy)
        75 -> "heavy_snow"
        // Snow grains
        77 -> "flurries"
        // Rain showers (slight, moderate)
        80, 81 -> "scattered_showers"
        // Rain showers (violent)
        82 -> "heavy"
        // Snow showers (slight)
        85 -> "scattered_snow"
        // Snow showers (heavy)
        86 -> "heavy_snow"
        // Thunderstorm
        95 -> "strong_tstorms"
        // Thunderstorm with hail
        96, 99 -> "strong_tstorms"
        // Fallback
        else -> "cloudy"
    }
}
