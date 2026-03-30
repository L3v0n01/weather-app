package com.la.weather.feature.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.la.weather.core.designsystem.icon.WeatherIcon
import com.la.weather.core.model.settings.TemperatureUnit
import com.la.weather.core.model.weather.DailyForecast
import com.la.weather.core.model.weather.HourlyForecast
import com.la.weather.core.model.weather.WeatherForecast
import androidx.compose.ui.tooling.preview.Preview
import com.la.weather.core.designsystem.theme.WeatherTheme
import com.la.weather.core.model.weather.CurrentWeather
import com.la.weather.core.model.weather.WeatherCondition
import kotlinx.collections.immutable.persistentListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.handleEvent(HomeUiEvent.Refresh)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                HomeUiEffect.RequestLocationPermission -> permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                cityName = uiState.cityName.ifEmpty { stringResource(R.string.home_default_title) },
                onSearchClick = onNavigateToSearch,
                onSettingsClick = onNavigateToSettings,
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                uiState.isLoading -> LoadingContent()
                uiState.forecast != null -> WeatherContent(
                    forecast = uiState.forecast!!,
                    temperatureUnit = uiState.temperatureUnit,
                    onRefresh = { viewModel.handleEvent(HomeUiEvent.Refresh) },
                )

                uiState.errorMessage != null -> ErrorContent(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.handleEvent(HomeUiEvent.Refresh) },
                    onSearchCity = onNavigateToSearch,
                )
                uiState.errorResId != null -> ErrorContent(
                    message = stringResource(uiState.errorResId!!),
                    onRetry = { viewModel.handleEvent(HomeUiEvent.Refresh) },
                    onSearchCity = onNavigateToSearch,
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    cityName: String,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(64.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = cityName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                maxLines = 1,
            )
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = stringResource(R.string.home_cd_search))
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.home_cd_settings))
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onSearchCity: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.home_btn_retry))
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onSearchCity) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.home_btn_search_city))
        }
    }
}

@Composable
private fun WeatherContent(
    forecast: WeatherForecast,
    temperatureUnit: TemperatureUnit,
    onRefresh: () -> Unit,
) {
    val current = forecast.current
    // Show only next 24 hourly entries
    val hourlyItems = forecast.hourly.take(24)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // Current weather hero
        item {
            CurrentWeatherCard(
                forecast = forecast,
                temperatureUnit = temperatureUnit,
                onRefresh = onRefresh,
            )
        }

        // Hourly forecast section
        item {
            Text(
                text = stringResource(R.string.home_section_hourly),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            HourlyForecastRow(items = hourlyItems, temperatureUnit = temperatureUnit)
        }

        // Daily forecast section header
        item {
            Text(
                text = stringResource(R.string.home_section_daily),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            )
        }

        // Daily forecast items
        items(forecast.daily) { day ->
            DailyForecastRow(item = day, temperatureUnit = temperatureUnit)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun CurrentWeatherCard(
    forecast: WeatherForecast,
    temperatureUnit: TemperatureUnit,
    onRefresh: () -> Unit,
) {
    val current = forecast.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WeatherIcon(
                wmoCode = current.weatherCondition.code,
                isDay = current.isDay,
                size = 120.dp,
                contentDescription = current.weatherCondition.description,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = current.temperature.formatTemp(temperatureUnit),
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Text(
                text = current.weatherCondition.description,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                WeatherStat(
                    label = stringResource(R.string.home_label_feels_like),
                    value = current.apparentTemperature.formatTemp(temperatureUnit)
                )
                WeatherStat(
                    label = stringResource(R.string.home_label_humidity),
                    value = "${current.humidity}%"
                )
                WeatherStat(
                    label = stringResource(R.string.home_label_wind),
                    value = "${current.windSpeed.roundToInt()} ${stringResource(R.string.home_unit_kmh)}"
                )
                WeatherStat(
                    label = stringResource(R.string.home_label_rain),
                    value = "${current.precipitation} ${stringResource(R.string.home_unit_mm)}"
                )
            }
        }
    }
}

@Composable
private fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun HourlyForecastRow(items: List<HourlyForecast>, temperatureUnit: TemperatureUnit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items) { hour ->
            HourlyForecastCard(item = hour, temperatureUnit = temperatureUnit)
        }
    }
}

@Composable
private fun HourlyForecastCard(item: HourlyForecast, temperatureUnit: TemperatureUnit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = item.time.toHourLabel(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            WeatherIcon(
                wmoCode = item.weatherCondition.code,
                isDay = true,
                size = 32.dp,
                contentDescription = item.weatherCondition.description,
            )
            Text(
                text = item.temperature.formatTemp(temperatureUnit),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            // Always render so all cards have the same height
            Text(
                text = if (item.precipitationProbability > 0) "${item.precipitationProbability}%" else "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun DailyForecastRow(item: DailyForecast, temperatureUnit: TemperatureUnit) {
    val todayLabel = stringResource(R.string.home_label_today)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.date.toDayLabel(todayLabel),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        WeatherIcon(
            wmoCode = item.weatherCondition.code,
            isDay = true,
            size = 28.dp,
            modifier = Modifier.padding(horizontal = 8.dp),
            contentDescription = item.weatherCondition.description,
        )
        if (item.precipitationProbabilityMax > 0) {
            Text(
                text = "${item.precipitationProbabilityMax}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .width(36.dp)
                    .padding(end = 4.dp),
            )
        } else {
            Spacer(modifier = Modifier.width(36.dp))
        }
        Text(
            text = item.temperatureMin.formatTemp(temperatureUnit),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(48.dp),
            textAlign = TextAlign.End,
        )
        Text(
            text = stringResource(R.string.home_temp_separator),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = item.temperatureMax.formatTemp(temperatureUnit),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(48.dp),
        )
    }
}

// Helper extensions

private fun Double.formatTemp(unit: TemperatureUnit): String =
    if (unit == TemperatureUnit.FAHRENHEIT) {
        "${(this * 9.0 / 5.0 + 32).roundToInt()}°F"
    } else {
        "${roundToInt()}°C"
    }

/** "2025-03-27T14:00" → "14:00" */
private fun String.toHourLabel(): String =
    if (length >= 16) substring(11, 16) else this

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val sampleForecast = WeatherForecast(
        latitude = 40.71,
        longitude = -74.01,
        timezone = "America/New_York",
        current = CurrentWeather(
            temperature = 18.0,
            apparentTemperature = 16.5,
            humidity = 65,
            windSpeed = 12.0,
            precipitation = 0.0,
            weatherCondition = WeatherCondition.PARTLY_CLOUDY,
            isDay = true,
            time = "2025-03-27T14:00",
        ),
        hourly = persistentListOf(
            HourlyForecast("2025-03-27T14:00", 18.0, WeatherCondition.PARTLY_CLOUDY, 10, 12.0),
            HourlyForecast("2025-03-27T15:00", 19.0, WeatherCondition.MAINLY_CLEAR, 5, 10.0),
            HourlyForecast("2025-03-27T16:00", 17.0, WeatherCondition.OVERCAST, 20, 14.0),
        ),
        daily = persistentListOf(
            DailyForecast("2025-03-27", 20.0, 12.0, WeatherCondition.PARTLY_CLOUDY, "06:30", "18:45", 0.0, 10),
            DailyForecast("2025-03-28", 22.0, 14.0, WeatherCondition.CLEAR_SKY, "06:28", "18:46", 0.0, 0),
        ),
    )
    WeatherTheme {
        WeatherContent(
            forecast = sampleForecast,
            temperatureUnit = TemperatureUnit.CELSIUS,
            onRefresh = {},
        )
    }
}

/** "2025-03-27" → "Mon" / todayLabel */
private fun String.toDayLabel(todayLabel: String): String {
    if (length < 10) return this
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    if (this == today) return todayLabel
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)
        SimpleDateFormat("EEE", Locale.getDefault()).format(date!!)
    } catch (e: Exception) {
        substring(5)
    }
}
