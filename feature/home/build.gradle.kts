plugins {
    id("weather.android.feature")
    id("weather.jacoco")
}

android {
    namespace = "com.la.weather.feature.home"
}

dependencies {
    implementation(project(":core:location"))
    implementation(project(":core:datastore"))
    testImplementation(project(":core:testing"))
}
