plugins {
    id("financeos.android.library")
    id("financeos.compose")
    id("financeos.koin")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.daprox.financeos.presentation"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))

    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.compose.ui.text.google.fonts)
}
