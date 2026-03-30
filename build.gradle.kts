plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    jacoco
}

private val jacocoExclusions = listOf(
    "**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*",
    "**/di/**", "**/hilt_aggregated_deps/**",
    "**/*Hilt*.*", "**/Dagger*.*", "**/*_Factory*.*", "**/*_MembersInjector*.*",
)

// Run all module-level jacocoTestReport tasks then produce a merged report.
// Usage:   ./gradlew jacocoTestReport
// XML CI:  build/reports/jacoco/jacoco.xml
tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Generates merged JaCoCo HTML + XML report across all modules"

    dependsOn(subprojects.mapNotNull { it.tasks.findByName("jacocoTestReport") })

    classDirectories.setFrom(
        subprojects.map { proj ->
            proj.fileTree(proj.layout.buildDirectory.get().asFile) {
                include(
                    // JVM modules (core:model, core:domain)
                    "classes/kotlin/main/**/*.class",
                    // Android modules (AGP 8+/9+)
                    "intermediates/runtime_library_classes_dir/debug/**/*.class",
                    "intermediates/javac/debug/**/*.class",
                )
                exclude(jacocoExclusions)
            }
        }
    )
    sourceDirectories.setFrom(
        subprojects.flatMap { proj ->
            listOf("src/main/java", "src/main/kotlin")
                .map { proj.file(it) }
                .filter { it.exists() }
        }
    )
    executionData.setFrom(
        subprojects.map { proj ->
            proj.fileTree(proj.layout.buildDirectory.get().asFile) {
                include(
                    "jacoco/testDebugUnitTest.exec",
                    "jacoco/test.exec",
                    "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                )
            }
        }
    )
    reports {
        html.required.set(true)
        xml.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacoco.xml"))
    }
}
