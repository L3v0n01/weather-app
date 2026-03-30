import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

private val JACOCO_EXCLUSIONS = listOf(
    "**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*",
    "**/di/**", "**/hilt_aggregated_deps/**",
    "**/*Hilt*.*", "**/Dagger*.*", "**/*_Factory*.*",
    "**/*_MembersInjector*.*", "**/BR.class",
)

class JacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("jacoco")

            configure<JacocoPluginExtension> {
                toolVersion = "0.8.11"
            }

            // Enable coverage data collection on every Test task
            tasks.withType<Test>().configureEach {
                extensions.configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }

            // For JVM modules the jacoco plugin already registers jacocoTestReport;
            // for Android modules it does not — register it ourselves.
            afterEvaluate {
                if (tasks.findByName("jacocoTestReport") == null) {
                    registerJacocoReportTask()
                } else {
                    // Configure the existing task that the jacoco plugin created
                    tasks.named("jacocoTestReport", JacocoReport::class.java) {
                        configureReport()
                    }
                }
            }
        }
    }

    private fun Project.registerJacocoReportTask() {
        tasks.register("jacocoTestReport", JacocoReport::class.java) {
            configureReport()
        }
    }

    private fun JacocoReport.configureReport() {
        group = "verification"
        description = "Generates JaCoCo HTML and XML reports for this module"

        val testTask = project.tasks.findByName("testDebugUnitTest")
            ?: project.tasks.findByName("test")
        testTask?.let { dependsOn(it) }

        classDirectories.setFrom(
            project.fileTree(project.layout.buildDirectory.get().asFile) {
                include(
                    "classes/kotlin/main/**/*.class",
                    "intermediates/runtime_library_classes_dir/debug/**/*.class",
                    "intermediates/javac/debug/**/*.class",
                )
                exclude(JACOCO_EXCLUSIONS)
            }
        )
        sourceDirectories.setFrom(
            project.file("src/main/java").takeIf { it.exists() },
            project.file("src/main/kotlin").takeIf { it.exists() },
        )
        executionData.setFrom(
            project.fileTree(project.layout.buildDirectory.get().asFile) {
                include(
                    "jacoco/testDebugUnitTest.exec",
                    "jacoco/test.exec",
                    "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                )
            }
        )
        reports {
            html.required.set(true)
            xml.required.set(true)
            html.outputLocation.set(project.layout.buildDirectory.dir("reports/jacoco/html"))
            xml.outputLocation.set(project.layout.buildDirectory.file("reports/jacoco/jacoco.xml"))
        }
    }
}
