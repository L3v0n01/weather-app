package com.la.weather.core.model.location

data class City(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val countryCode: String,
    val admin1: String?,
    val timezone: String,
    val population: Int?,
)
