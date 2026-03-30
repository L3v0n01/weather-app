# Weather

An Android weather app built with Jetpack Compose and Clean Architecture. Uses the [Open-Meteo](https://open-meteo.com/) API (no API key required) to display current conditions, hourly forecasts, and a 7-day outlook.

## Features

- Current weather with temperature, humidity, wind speed, and precipitation
- 24-hour hourly forecast
- 7-day daily forecast
- City search via geocoding
- Device location support (GPS / network)
- Celsius / Fahrenheit toggle
- Light / Dark / System theme modes
- Offline caching with Room + automatic refetch when connectivity is restored
- Real-time network state monitoring with offline banner indicator
- SVG weather icons (44 Google weather icons with light/dark variants)

## Screenshots

_Coming soon_

## Architecture

Multi-module Clean Architecture with the **MVI** (Model-View-Intent) pattern.

```
app
├── feature:home          # Main weather display
├── feature:search        # City search
├── feature:settings      # Theme & unit preferences
├── core:domain           # Use cases & repository interfaces (pure Kotlin/JVM)
├── core:model            # Domain models & Resource<T> (pure Kotlin/JVM)
├── core:data             # Repository implementations & mappers
├── core:network          # Retrofit APIs (Open-Meteo)
├── core:database         # Room (weather cache)
├── core:datastore        # DataStore Preferences
├── core:location         # FusedLocationProviderClient wrapper
├── core:common           # BaseViewModel, MVI contracts, dispatchers
├── core:designsystem     # Compose theme, components, weather icons
├── core:navigation       # Navigation 3 type-safe routes
└── core:testing          # Shared test utilities
```

### Key Design Decisions

- **Resource\<T\>** is a sealed interface with only `Success` and `Error` — no `Loading` variant. Loading is UI state, not a data result.
- **Repositories return `suspend fun`: Resource\<T\>**, not Flow, since the app is not offline-first.
- **Navigation 3** with `NavDisplay`, type-safe routes, and entry decorators for saved state and ViewModel scoping.
- **Convention plugins** (`build-logic/`) enforce consistent SDK versions, Hilt wiring, Compose config, and JaCoCo coverage across all modules.
- **Weather icons** are SVGs rendered at runtime via Coil's SVG decoder (complex gradients prevent VectorDrawable conversion).

## Tech Stack

| Category | Libraries |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose (BOM 2025.05.01), Material 3 |
| Navigation | Navigation 3 1.0.0 |
| DI | Hilt 2.59 + KSP |
| Networking | Retrofit 3.0.0, OkHttp 4.12, kotlinx.serialization |
| Database | Room 2.7.1 |
| Preferences | DataStore |
| Location | Google Play Services Location 21.3.0 |
| Images | Coil 3.0.4 (SVG decoder) |
| Async | Kotlin Coroutines 1.9.0 |
| Testing | JUnit 5, MockK, Turbine, Kotest Property |
| Coverage | JaCoCo 0.8.11 (per-module CSV/HTML/XML reports) |
| Build | Gradle 9.3.1, AGP 9.1.0, Convention Plugins |
| CI | GitHub Actions |

## Build

```bash
# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest

# Run lint
./gradlew lint

# Run instrumented tests (requires device/emulator)
./gradlew connectedCheck
```

## Project Setup

No API key is needed. The app uses the free [Open-Meteo API](https://open-meteo.com/) for weather data and geocoding.

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on a device or emulator (minSdk 24)

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
