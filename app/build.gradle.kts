import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinMultiplatform)
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        val desktopTest by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.materialIconsExtended)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
        }

        desktopTest.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(kotlin("test"))

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)

            implementation(compose.desktop.uiTestJUnit4)
            implementation("org.mockito:mockito-core:5.13.0")
            implementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
        }
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
