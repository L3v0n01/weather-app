plugins {
    id("weather.jvm.library")
    id("weather.jacoco")
}

dependencies {
    api(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
    api(libs.javax.inject)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.collections.immutable)
}
