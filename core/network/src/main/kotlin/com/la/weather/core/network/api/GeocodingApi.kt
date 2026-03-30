package com.la.weather.core.network.api

import com.la.weather.core.network.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
    ): GeocodingResponseDto
}