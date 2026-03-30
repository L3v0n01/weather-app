package com.la.weather.core.model.settings

enum class TemperatureUnit(val apiValue: String) {
    CELSIUS("celsius"),
    FAHRENHEIT("fahrenheit");

    companion object {
        val DEFAULT = CELSIUS

        fun fromApiValue(value: String): TemperatureUnit =
            entries.find { it.apiValue == value } ?: DEFAULT
    }
}
