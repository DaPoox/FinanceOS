import org.gradle.api.Plugin
import org.gradle.api.Project

// Applies to :domain and :core. Pure Kotlin JVM — zero Android dependencies enforced by the module graph.
class DomainModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
        }
    }
}
