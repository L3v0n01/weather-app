plugins {
    id("weather.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.la.weather.core.designsystem"
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.icons.extended)
    api(libs.lottie.compose)
    api(libs.coil.compose)
    api(libs.kotlinx.collections.immutable)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
