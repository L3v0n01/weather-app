package com.la.weather.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
) : LocationProvider {

    override suspend fun getCurrentLocation(): Result<DeviceLocation> {
        if (!hasLocationPermission()) {
            return Result.failure(LocationException(LocationError.PermissionDenied))
        }

        if (!isGpsEnabled()) {
            return Result.failure(LocationException(LocationError.GpsDisabled))
        }

        return try {
            val location = awaitCurrentLocation()
            if (location != null) {
                val cityName = reverseGeocode(location.latitude, location.longitude)
                Result.success(DeviceLocation(location.latitude, location.longitude, cityName))
            } else {
                Result.failure(LocationException(LocationError.Unavailable))
            }
        } catch (e: SecurityException) {
            Result.failure(LocationException(LocationError.PermissionDenied))
        } catch (e: Exception) {
            Result.failure(LocationException(LocationError.Unavailable))
        }
    }

    @Suppress("MissingPermission")
    private suspend fun awaitCurrentLocation(): Location? =
        suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cts.token,
            ).addOnSuccessListener { location ->
                cont.resume(location)
            }.addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
            cont.invokeOnCancellation { cts.cancel() }
        }

    private suspend fun reverseGeocode(latitude: Double, longitude: Double): String =
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val name = addresses.firstOrNull()?.locality
                            ?: addresses.firstOrNull()?.subAdminArea
                            ?: addresses.firstOrNull()?.adminArea
                            ?: ""
                        cont.resume(name)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.locality
                    ?: addresses?.firstOrNull()?.subAdminArea
                    ?: addresses?.firstOrNull()?.adminArea
                    ?: ""
            }
        } catch (_: Exception) {
            ""
        }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    private fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
