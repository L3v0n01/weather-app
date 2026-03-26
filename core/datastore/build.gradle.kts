plugins {
    id("weather.android.library")
    id("weather.android.hilt")
}

android {
    namespace = "com.la.weather.core.datastore"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.datastore.preferences)
}
