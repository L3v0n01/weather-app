package com.la.weather.core.data.mapper

import com.la.weather.core.model.location.City
import com.la.weather.core.network.dto.GeocodingResultDto

// DTO → Domain

fun GeocodingResultDto.toCity(): City = City(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    country = country ?: "",
    countryCode = countryCode ?: "",
    admin1 = admin1,
    timezone = timezone ?: "UTC",
    population = population,
)