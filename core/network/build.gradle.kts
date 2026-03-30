plugins {
    id("weather.android.library")
    id("weather.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.la.weather.core.network"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
}
