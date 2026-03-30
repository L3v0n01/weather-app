package com.la.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.la.weather.core.designsystem.theme.WeatherTheme
import com.la.weather.core.model.settings.ThemeMode
import com.la.weather.core.navigation.HomeRoute
import com.la.weather.core.navigation.SearchRoute
import com.la.weather.core.navigation.SettingsRoute
import com.la.weather.feature.home.HomeScreen
import com.la.weather.feature.home.HomeUiEvent
import com.la.weather.feature.home.HomeViewModel
import com.la.weather.feature.search.SearchScreen
import com.la.weather.feature.settings.SettingsScreen
import com.la.weather.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle()

            WeatherTheme(themeMode = themeMode?:ThemeMode.SYSTEM) {
                WeatherNavigation()
            }
        }
    }
}

@Composable
private fun WeatherNavigation() {
    val backStack = rememberNavBackStack(HomeRoute)
    val homeViewModel: HomeViewModel = hiltViewModel()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToSearch = { backStack.add(SearchRoute) },
                    onNavigateToSettings = { backStack.add(SettingsRoute) },
                )
            }
            entry<SearchRoute> {
                SearchScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onCitySelected = { city ->
                        homeViewModel.handleEvent(HomeUiEvent.CitySelected(city))
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<SettingsRoute> {
                SettingsScreen(
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
