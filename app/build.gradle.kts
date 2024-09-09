import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the org.jetbrains.compose plugin to add support for Compose for Desktop.
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    // This dependency is used by the application.
    implementation(libs.guava)

    // This dependency is used by Jetpack Compose for Desktop.
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
}

tasks.test {
    useJUnitPlatform()
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "text-editor"
            packageVersion = "0.0.1"
        }
    }
}
