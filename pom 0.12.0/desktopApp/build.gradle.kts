plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
}
kotlin { jvmToolchain(17) }
dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
}
compose.desktop {
    application {
        mainClass = "it.prometeo.desktop.MainKt"
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi)
            packageName = "PROMETEO ULTIMATE"
            packageVersion = "0.2.0"
            description = "Hub personale PROMETEO ULTIMATE"
            vendor = "PROMETEO"
        }
    }
}
