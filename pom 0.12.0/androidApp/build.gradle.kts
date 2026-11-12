plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
}
android {
    namespace = "it.prometeo.ultimate"
    compileSdk = 35
    defaultConfig {
        applicationId = "it.prometeo.ultimate"
        minSdk = 26
        targetSdk = 35
        versionCode = 9
        versionName = "0.12.0-adaptive"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin { jvmToolchain(17) }
dependencies {
    implementation(project(":shared"))
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation("androidx.activity:activity-compose:1.10.0")
}
