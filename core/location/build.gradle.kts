plugins {
    id("weather.android.library")
    id("weather.android.hilt")
}

android {
    namespace = "com.la.weather.core.location"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.android)
}
