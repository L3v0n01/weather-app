package com.la.weather.core.location

interface LocationProvider {
    suspend fun getCurrentLocation(): Result<DeviceLocation>
}
