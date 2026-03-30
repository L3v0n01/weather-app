plugins {
    id("weather.android.feature")
    id("weather.jacoco")
}

android {
    namespace = "com.la.weather.feature.search"
}

dependencies {
    testImplementation(project(":core:testing"))
}
