plugins {
    id("weather.android.feature")
}

android {
    namespace = "com.la.weather.feature.search"
}

dependencies {
    testImplementation(project(":core:testing"))
}
