package com.la.weather.core.model.settings

enum class ThemeMode(val apiValue: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {

        fun fromApiValue(value: String): ThemeMode =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: SYSTEM
    }
}