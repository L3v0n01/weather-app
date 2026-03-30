package com.la.weather.feature.home

import androidx.lifecycle.viewModelScope
import com.la.weather.core.common.connectivity.ConnectivityObserver
import com.la.weather.core.common.mvi.BaseViewModel
import com.la.weather.core.datastore.WeatherPreferences
import com.la.weather.core.domain.usecase.GetWeatherUseCase
import com.la.weather.core.location.LocationError
import com.la.weather.core.location.LocationException
import com.la.weather.core.location.LocationProvider
import com.la.weather.core.model.location.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val locationProvider: LocationProvider,
    private val weatherPreferences: WeatherPreferences,
    private val connectivityObserver: ConnectivityObserver,
) : BaseViewModel<HomeUiState, HomeUiEvent, HomeUiEffect>() {

    override fun initialState() = HomeUiState()

    companion object {
        private const val LOCATION_TIMEOUT_MS = 10_000L
    }

    init {
        observeTemperatureUnit()
        observeConnectivity()
        loadInitialWeather()
    }

    override fun handleEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Refresh -> loadInitialWeather()
            is HomeUiEvent.CitySelected -> loadWeatherForCity(event.city)
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.isOnline.collect { online ->
                if (online && uiState.value.isOffline) {
                    loadInitialWeather()
                } else if (!online && uiState.value.forecast != null) {
                    updateState { copy(isOffline = true) }
                }
            }
        }
    }

    private fun observeTemperatureUnit() {
        viewModelScope.launch {
            weatherPreferences.temperatureUnit.collect { unit ->
                updateState { copy(temperatureUnit = unit) }
            }
        }
    }

    private fun loadInitialWeather() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorResId = null, errorMessage = null) }
            val savedLat = weatherPreferences.lastLatitude.first()
            val savedLon = weatherPreferences.lastLongitude.first()
            val savedCity = weatherPreferences.lastCityName.first() ?: ""
            if (savedLat != null && savedLon != null) {
                fetchWeather(savedLat, savedLon, savedCity)
            } else {
                fetchWeatherFromDeviceLocation()
            }
        }
    }

    private suspend fun fetchWeatherFromDeviceLocation() {
        val locationResult = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            locationProvider.getCurrentLocation()
        } ?: Result.failure(LocationException(LocationError.Unavailable))

        if (locationResult.isSuccess) {
            val loc = locationResult.getOrThrow()
            weatherPreferences.setLastLocation(loc.latitude, loc.longitude, loc.cityName)
            fetchWeather(loc.latitude, loc.longitude, loc.cityName)
        } else {
            val throwable = locationResult.exceptionOrNull()
            if ((throwable as? LocationException)?.error == LocationError.PermissionDenied) {
                sendEffect(HomeUiEffect.RequestLocationPermission)
                updateState { copy(isLoading = false, errorResId = R.string.home_error_permission) }
            } else {
                updateState { copy(isLoading = false, errorResId = R.string.home_error_location) }
            }
        }
    }

    private fun fetchWeather(lat: Double, lon: Double, cityName: String) {
        execute(
            block = { getWeatherUseCase(lat, lon) },
            onSuccess = { forecast ->
                val isCurrentlyOffline = !connectivityObserver.isOnline.first()
                updateState {
                    copy(
                        isLoading = false,
                        forecast = forecast,
                        cityName = cityName,
                        isOffline = isCurrentlyOffline
                    )
                }
            },
            onError = { msg, _ ->
                updateState { copy(isLoading = false, errorMessage = msg) }
            },
        )
    }

    private fun loadWeatherForCity(city: City) {
        viewModelScope.launch {
            weatherPreferences.setLastLocation(city.latitude, city.longitude, city.name)
        }
        updateState {
            copy(
                isLoading = true,
                errorResId = null,
                errorMessage = null,
                cityName = city.name
            )
        }
        execute(
            block = { getWeatherUseCase(city.latitude, city.longitude) },
            onSuccess = { forecast ->
                val isCurrentlyOffline = !connectivityObserver.isOnline.first()
                updateState {
                    copy(
                        isLoading = false,
                        forecast = forecast,
                        cityName = city.name,
                        isOffline = isCurrentlyOffline
                    )
                }
            },
            onError = { msg, _ ->
                updateState { copy(isLoading = false, errorMessage = msg) }
            },
        )
    }
}
