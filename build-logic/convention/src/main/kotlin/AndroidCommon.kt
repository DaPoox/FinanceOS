import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion

// Single source of truth for Android SDK targets and JVM compatibility.
// AGP 9.0 removed these from CommonExtension, so we use overloads for each extension type.

private const val COMPILE_SDK = 36
private const val MIN_SDK = 27
private val JVM_TARGET = JavaVersion.VERSION_11

internal fun ApplicationExtension.configureAndroid() {
    compileSdk = COMPILE_SDK
    defaultConfig.minSdk = MIN_SDK
    compileOptions {
        sourceCompatibility = JVM_TARGET
        targetCompatibility = JVM_TARGET
    }
}

internal fun LibraryExtension.configureAndroid() {
    compileSdk = COMPILE_SDK
    defaultConfig.minSdk = MIN_SDK
    compileOptions {
        sourceCompatibility = JVM_TARGET
        targetCompatibility = JVM_TARGET
    }
}
