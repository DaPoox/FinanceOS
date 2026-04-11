plugins {
    `kotlin-dsl`
}

group = "com.daprox.financeos.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    // AGP and Kotlin are compileOnly — available at build time, not bundled
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "financeos.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "financeos.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("domainModule") {
            id = "financeos.domain.module"
            implementationClass = "DomainModuleConventionPlugin"
        }
        register("compose") {
            id = "financeos.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("koin") {
            id = "financeos.koin"
            implementationClass = "KoinConventionPlugin"
        }
        register("room") {
            id = "financeos.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}
