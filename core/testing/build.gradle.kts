plugins {
    id("weather.android.library")
}

android {
    namespace = "com.la.weather.core.testing"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    api(libs.junit)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.kotlinx.coroutines.test)
}
