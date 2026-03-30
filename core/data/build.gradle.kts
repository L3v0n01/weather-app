plugins {
    id("weather.android.library")
    id("weather.android.hilt")
    id("weather.jacoco")
}

android {
    namespace = "com.la.weather.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.retrofit)

    testImplementation(project(":core:testing"))
    testImplementation(libs.kotest.property)
}
