package com.la.weather.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.la.weather.core.designsystem.theme.WeatherTheme
import com.la.weather.core.model.settings.TemperatureUnit
import com.la.weather.core.model.settings.ThemeMode

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .height(64.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_cd_back),
                        )
                    }
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                SettingsRow(label = stringResource(R.string.settings_label_temperature)) {
                    FilterChip(
                        selected = uiState.temperatureUnit == TemperatureUnit.CELSIUS,
                        onClick = { viewModel.handleEvent(SettingsUiEvent.TemperatureUnitChanged(TemperatureUnit.CELSIUS)) },
                        label = { ChipTextLabel(stringResource(R.string.settings_unit_celsius)) },
                        modifier = Modifier.width(CHIP_WIDTH),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = uiState.temperatureUnit == TemperatureUnit.FAHRENHEIT,
                        onClick = { viewModel.handleEvent(SettingsUiEvent.TemperatureUnitChanged(TemperatureUnit.FAHRENHEIT)) },
                        label = { ChipTextLabel(stringResource(R.string.settings_unit_fahrenheit)) },
                        modifier = Modifier.width(CHIP_WIDTH),
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            item {
                SettingsRow(label = stringResource(R.string.settings_label_theme)) {
                    FilterChip(
                        selected = uiState.themeMode == ThemeMode.LIGHT,
                        onClick = { viewModel.handleEvent(SettingsUiEvent.ThemeModeChanged(ThemeMode.LIGHT)) },
                        label = { ChipIconLabel(Icons.Default.LightMode, stringResource(R.string.settings_theme_light)) },
                        modifier = Modifier.width(CHIP_WIDTH),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = uiState.themeMode == ThemeMode.DARK,
                        onClick = { viewModel.handleEvent(SettingsUiEvent.ThemeModeChanged(ThemeMode.DARK)) },
                        label = { ChipIconLabel(Icons.Default.DarkMode, stringResource(R.string.settings_theme_dark)) },
                        modifier = Modifier.width(CHIP_WIDTH),
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    chips: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Row(verticalAlignment = Alignment.CenterVertically) { chips() }
    }
}

@Composable
private fun ChipTextLabel(text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}

@Composable
private fun ChipIconLabel(icon: ImageVector, contentDescription: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Icon(imageVector = icon, contentDescription = contentDescription, modifier = Modifier.size(18.dp))
    }
}

private val CHIP_WIDTH: Dp = 56.dp

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    WeatherTheme {
        LazyColumn {
            item {
                SettingsRow(label = "Temperature") {
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { ChipTextLabel("°C") },
                        modifier = Modifier.width(CHIP_WIDTH),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { ChipTextLabel("°F") },
                        modifier = Modifier.width(CHIP_WIDTH),
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                SettingsRow(label = "Theme") {
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { ChipIconLabel(Icons.Default.LightMode, "Light") },
                        modifier = Modifier.width(CHIP_WIDTH),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { ChipIconLabel(Icons.Default.DarkMode, "Dark") },
                        modifier = Modifier.width(CHIP_WIDTH),
                    )
                }
            }
        }
    }
}
