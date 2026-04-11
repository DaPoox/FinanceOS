plugins {
    id("financeos.android.library")
    id("financeos.room")
    id("financeos.koin")
}

android {
    namespace = "com.daprox.financeos.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
}
