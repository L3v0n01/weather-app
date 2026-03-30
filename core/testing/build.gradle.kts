plugins {
    id("weather.android.library")
}

android {
    namespace = "com.la.weather.core.testing"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    api(libs.junit.jupiter.api)
    api(libs.junit.platform.launcher)
    runtimeOnly(libs.junit.jupiter.engine)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.kotlinx.coroutines.test)
    api(libs.kotest.property)
    api(libs.kotlinx.collections.immutable)
}
