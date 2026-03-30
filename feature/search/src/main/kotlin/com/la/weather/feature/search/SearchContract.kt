package com.la.weather.feature.search

import com.la.weather.core.common.mvi.UiEffect
import com.la.weather.core.common.mvi.UiEvent
import com.la.weather.core.common.mvi.UiState
import com.la.weather.core.model.location.City
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SearchUiState(
    val query: String = "",
    val cities: ImmutableList<City> = persistentListOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface SearchUiEvent : UiEvent {
    data class QueryChanged(val query: String) : SearchUiEvent
    data class CitySelected(val city: City) : SearchUiEvent
}

sealed interface SearchUiEffect : UiEffect {
    data class CitySelected(val city: City) : SearchUiEffect
}
