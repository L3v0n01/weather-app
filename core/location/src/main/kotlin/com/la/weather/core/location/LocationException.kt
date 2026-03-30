package com.la.weather.core.location

class LocationException(val error: LocationError) : Exception(
    when (error) {
        LocationError.PermissionDenied -> "Location permission denied"
        LocationError.GpsDisabled -> "GPS is disabled"
        LocationError.Unavailable -> "Location unavailable"
    },
)
