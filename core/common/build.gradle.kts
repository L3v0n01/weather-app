plugins {
    id("weather.android.library")
    id("weather.android.hilt")
}

android {
    namespace = "com.la.weather.core.common"
}

dependencies {
    api(project(":core:model"))
    api(libs.kotlinx.coroutines.android)
    api(libs.androidx.lifecycle.viewmodel.ktx)
}
