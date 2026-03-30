package com.la.weather.core.location

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
)

sealed interface LocationError {
    data object PermissionDenied : LocationError
    data object GpsDisabled : LocationError
    data object Unavailable : LocationError
}
