package com.la.weather.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.la.weather.core.designsystem.theme.WeatherTheme
import com.la.weather.core.model.location.City
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onCitySelected: (City) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.effects.collect { effect ->
            when (effect) {
                is SearchUiEffect.CitySelected -> onCitySelected(effect.city)
            }
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = uiState.query,
                isLoading = uiState.isLoading,
                focusRequester = focusRequester,
                onQueryChanged = { viewModel.handleEvent(SearchUiEvent.QueryChanged(it)) },
                onBack = onBack,
            )
        }
    ) { paddingValues ->
        SearchContent(
            uiState = uiState,
            onCityClick = { viewModel.handleEvent(SearchUiEvent.CitySelected(it)) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@Composable
private fun SearchTopBar(
    query: String,
    isLoading: Boolean,
    focusRequester: FocusRequester,
    onQueryChanged: (String) -> Unit,
    onBack: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.search_cd_back),
                    )
                }
                TextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { }),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChanged("") }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.search_cd_clear),
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            }
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun SearchContent(
    uiState: SearchUiState,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when {
            uiState.error != null -> {
                Text(
                    text = uiState.error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                )
            }
            uiState.query.isEmpty() -> {
                Text(
                    text = stringResource(R.string.search_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                )
            }
            uiState.cities.isEmpty() && !uiState.isLoading && uiState.query.length >= 2 -> {
                Text(
                    text = stringResource(R.string.search_no_results, uiState.query),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.cities) { city ->
                        CityListItem(
                            city = city,
                            onClick = { onCityClick(city) },
                        )
                        HorizontalDivider()
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CityListItem(city: City, onClick: () -> Unit) {
    val subtitle = buildString {
        if (!city.admin1.isNullOrEmpty()) append(city.admin1).append(", ")
        append(city.country)
        city.population?.let { pop ->
            if (pop > 0) append(" · ${formatPopulation(pop)}")
        }
    }

    ListItem(
        headlineContent = {
            Text(text = city.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
}

private fun formatPopulation(pop: Int): String = when {
    pop >= 1_000_000 -> "${pop / 1_000_000}M"
    pop >= 1_000 -> "${pop / 1_000}K"
    else -> pop.toString()
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    val sampleCities = persistentListOf(
        City(1, "New York", 40.71, -74.01, "United States", "US", "New York", "America/New_York", 8_336_817),
        City(2, "London", 51.51, -0.13, "United Kingdom", "GB", "England", "Europe/London", 8_982_000),
    )
    WeatherTheme {
        LazyColumn {
            items(sampleCities) { city ->
                CityListItem(city = city, onClick = {})
                HorizontalDivider()
            }
        }
    }
}