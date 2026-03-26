plugins {
    id("weather.android.library")
    id("weather.android.hilt")
}

android {
    namespace = "com.la.weather.core.database"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
