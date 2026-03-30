@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.la.weather.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    val results: List<GeocodingResultDto>? = null,
)

@Serializable
data class GeocodingResultDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null,
    @SerialName("feature_code") val featureCode: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    val timezone: String? = null,
    val population: Int? = null,
    val postcodes: List<String>? = null,
    val country: String? = null,
    val admin1: String? = null,
    val admin2: String? = null,
    val admin3: String? = null,
    val admin4: String? = null,
)
