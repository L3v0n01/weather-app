package com.la.weather.feature.search

import androidx.lifecycle.viewModelScope
import com.la.weather.core.common.mvi.BaseViewModel
import com.la.weather.core.domain.usecase.SearchCitiesUseCase
import com.la.weather.core.model.location.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCitiesUseCase: SearchCitiesUseCase,
) : BaseViewModel<SearchUiState, SearchUiEvent, SearchUiEffect>() {

    override fun initialState() = SearchUiState()

    private val queryFlow = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    override fun handleEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.QueryChanged -> {
                updateState {
                    copy(
                        query = event.query,
                        isLoading = event.query.length >= MIN_QUERY_LENGTH,
                        cities = if (event.query.isEmpty()) persistentListOf() else cities,
                        error = null,
                    )
                }
                queryFlow.value = event.query
            }
            is SearchUiEvent.CitySelected -> selectCity(event.city)
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            queryFlow
                .debounce(SEARCH_DEBOUNCE_MS)
                .filter { it.length >= MIN_QUERY_LENGTH }
                .collect { query ->
                    updateState { copy(isLoading = true, error = null) }
                    execute(
                        block = { searchCitiesUseCase(query) },
                        onSuccess = { cities ->
                            updateState { copy(isLoading = false, cities = cities.toImmutableList()) }
                        },
                        onError = { msg, _ ->
                            updateState { copy(isLoading = false, error = msg) }
                        },
                    )
                }
        }
    }

    private fun selectCity(city: City) {
        viewModelScope.launch { sendEffect(SearchUiEffect.CitySelected(city)) }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 400L
        private const val MIN_QUERY_LENGTH = 2
    }
}
