plugins {
    id("weather.android.feature")
    id("weather.jacoco")
}

android {
    namespace = "com.la.weather.feature.settings"
}

dependencies {
    implementation(project(":core:datastore"))
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(project(":core:testing"))
}
