plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("weatherAndroidLibrary") {
            id = "weather.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("weatherAndroidFeature") {
            id = "weather.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("weatherAndroidHilt") {
            id = "weather.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("weatherJvmLibrary") {
            id = "weather.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("weatherJacoco") {
            id = "weather.jacoco"
            implementationClass = "JacocoConventionPlugin"
        }
    }
}
