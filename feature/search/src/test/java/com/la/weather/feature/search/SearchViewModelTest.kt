package com.la.weather.feature.search

import app.cash.turbine.test
import com.la.weather.core.domain.usecase.SearchCitiesUseCase
import com.la.weather.core.model.Resource
import com.la.weather.core.model.location.City
import com.la.weather.core.testing.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class SearchViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension(StandardTestDispatcher())

    private val searchCitiesUseCase: SearchCitiesUseCase = mockk()
    private val fakeCity = City(1L, "Paris", 48.8566, 2.3522, "France", "FR", null, "Europe/Paris", null)

    private fun createViewModel() = SearchViewModel(searchCitiesUseCase)

    @Test
    fun `initial state has empty query and no cities`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.uiState.test {
                awaitItem().also { state ->
                    assertTrue(state.query.isEmpty())
                    assertTrue(state.cities.isEmpty())
                    assertFalse(state.isLoading)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `QueryChanged updates query in state immediately`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.handleEvent(SearchUiEvent.QueryChanged("Pa"))
            advanceUntilIdle()

            vm.uiState.test {
                assertEquals("Pa", awaitItem().query)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `QueryChanged triggers API call after 400ms debounce for queries 2+ chars`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            coEvery { searchCitiesUseCase("Paris") } returns Resource.Success(listOf(fakeCity))
            val vm = createViewModel()
            advanceUntilIdle()

            vm.handleEvent(SearchUiEvent.QueryChanged("Paris"))
            advanceTimeBy(500)
            advanceUntilIdle()

            coVerify(exactly = 1) { searchCitiesUseCase("Paris") }
        }

    @Test
    fun `QueryChanged shorter than 2 chars does not trigger API call`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.handleEvent(SearchUiEvent.QueryChanged("P"))
            advanceTimeBy(500)
            advanceUntilIdle()

            coVerify(exactly = 0) { searchCitiesUseCase(any()) }
        }

    @Test
    fun `successful search populates cities state`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            coEvery { searchCitiesUseCase("Paris") } returns Resource.Success(listOf(fakeCity))
            val vm = createViewModel()
            advanceUntilIdle()

            vm.handleEvent(SearchUiEvent.QueryChanged("Paris"))
            advanceTimeBy(500)
            advanceUntilIdle()

            vm.uiState.test {
                awaitItem().also { state ->
                    assertFalse(state.isLoading)
                    assertEquals(1, state.cities.size)
                    assertEquals(fakeCity.name, state.cities[0].name)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `failed search sets error state`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            coEvery { searchCitiesUseCase(any()) } returns Resource.Error("network error")
            val vm = createViewModel()
            advanceUntilIdle()

            vm.handleEvent(SearchUiEvent.QueryChanged("Paris"))
            advanceTimeBy(500)
            advanceUntilIdle()

            vm.uiState.test {
                awaitItem().also { state ->
                    assertFalse(state.isLoading)
                    assertEquals("network error", state.error)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `CitySelected emits CitySelected effect`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.effects.test {
                vm.handleEvent(SearchUiEvent.CitySelected(fakeCity))
                advanceUntilIdle()
                assertEquals(fakeCity, (awaitItem() as SearchUiEffect.CitySelected).city)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `clearing query resets cities list`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            coEvery { searchCitiesUseCase("Paris") } returns Resource.Success(listOf(fakeCity))
            val vm = createViewModel()
            advanceUntilIdle()

            vm.handleEvent(SearchUiEvent.QueryChanged("Paris"))
            advanceTimeBy(500); advanceUntilIdle()

            vm.handleEvent(SearchUiEvent.QueryChanged(""))
            advanceUntilIdle()

            vm.uiState.test {
                awaitItem().also { state ->
                    assertTrue(state.query.isEmpty())
                    assertTrue(state.cities.isEmpty())
                }
                cancelAndIgnoreRemainingEvents()
            }
        }
}
