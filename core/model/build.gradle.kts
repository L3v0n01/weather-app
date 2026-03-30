plugins {
    id("weather.jvm.library")
    id("weather.jacoco")

}

dependencies {
    api(libs.kotlinx.collections.immutable)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotlinx.coroutines.test)
}
