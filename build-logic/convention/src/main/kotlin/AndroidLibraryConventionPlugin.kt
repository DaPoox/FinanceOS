import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// Applies to :data and :presentation. Sets AGP library defaults shared across the project.
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            if (extensions.findByName("kotlin") == null) {
                pluginManager.apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<LibraryExtension> {
                configureAndroid()
            }
        }
    }
}
