plugins {
    id("weather.android.feature")
}

android {
    namespace = "com.la.weather.feature.home"
}

dependencies {
    implementation(project(":core:location"))
    testImplementation(project(":core:testing"))
}
