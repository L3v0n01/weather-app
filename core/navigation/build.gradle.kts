plugins {
    id("weather.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.la.weather.core.navigation"
}

dependencies {
    api(libs.navigation3.runtime)
    api(libs.navigation3.ui)
    implementation(libs.kotlinx.serialization.json)
}
