plugins {
    id("weather.android.library")
    id("weather.android.hilt")
}

android {
    namespace = "com.la.weather.core.data"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(libs.kotlinx.coroutines.android)
}
