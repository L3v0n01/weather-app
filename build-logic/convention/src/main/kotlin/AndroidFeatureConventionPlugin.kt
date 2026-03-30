import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("weather.android.library")
            pluginManager.apply("weather.android.hilt")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                buildFeatures { compose = true }
            }

            pluginManager.withPlugin("org.jetbrains.kotlin.plugin.compose") {
                val ext = extensions.getByName("composeCompiler")
                @Suppress("UNCHECKED_CAST")
                val files = ext.javaClass.getMethod(
                    "getStabilityConfigurationFiles",
                ).invoke(ext) as org.gradle.api.provider.ListProperty<org.gradle.api.file.RegularFile>
                files.add(target.rootProject.layout.projectDirectory.file("compose_stability.conf"))
            }

            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:model"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:navigation"))

                val libs = project.extensions
                    .getByType(VersionCatalogsExtension::class.java)
                    .named("libs")
                add("implementation", libs.findLibrary("androidx.compose.material3").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtime.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())
                add("implementation", libs.findLibrary("hilt.navigation.compose").get())
                add("implementation", libs.findLibrary("navigation3.runtime").get())
                add("implementation", libs.findLibrary("navigation3.ui").get())
                add("implementation", libs.findLibrary("kotlinx.collections.immutable").get())
            }
        }
    }
}