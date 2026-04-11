import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// Applies to :app. Sets AGP application defaults shared across the project.
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            if (extensions.findByName("kotlin") == null) {
                pluginManager.apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<ApplicationExtension> {
                configureAndroid()
            }
        }
    }
}
