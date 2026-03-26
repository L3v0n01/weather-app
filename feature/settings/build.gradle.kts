plugins {
    id("weather.android.feature")
}

android {
    namespace = "com.la.weather.feature.settings"
}

dependencies {
    testImplementation(project(":core:testing"))
}
