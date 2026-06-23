import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

// Cadence — a complex, Spotify-style music app built from scratch to exercise the UI/nav/state patterns a
// code generator must learn (bottom-nav shell, carousels, collapsing headers, a persistent mini-player
// overlay, a playback state machine). Same opinionated stack (Compose + Decompose + KStateMachine). Now a
// TRUE multiplatform app: Android + iOS are the real targets; wasm is the in-browser editor preview.
plugins {
    kotlin("multiplatform") version "2.4.0"
    kotlin("plugin.serialization") version "2.4.0"
    kotlin("plugin.compose") version "2.4.0"
    id("org.jetbrains.compose") version "1.11.1"
    id("com.android.application") version "8.13.2" // last AGP 8.x — still supports single-module KMP (AGP 9 dropped it)
}

repositories {
    google()
    mavenCentral()
}

// Compose 1.11 + Decompose 3.5 are built against kotlinx-browser 0.3; a newer one changes the DOM-interop
// ABI → ComposeViewport silently fails to attach its canvas (blank preview). Pin it (same as todo-lab).
configurations.all {
    resolutionStrategy.force("org.jetbrains.kotlinx:kotlinx-browser:0.3")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    jvm() // unit-test harness for the shared commonMain (Player transport machine, ScreenMachine, repository)
    androidTarget() // the Android target → APK (buildable on this Linux box with the SDK)
    // iOS targets — the REAL mobile target. Declaring them is fine on Linux; only the compile/link needs macOS.
    listOf(iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework { baseName = "MusicApp"; isStatic = true }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
                implementation("io.github.nsk90:kstatemachine:0.38.1")
                implementation("io.github.nsk90:kstatemachine-coroutines:0.38.1")
                // the shared Compose UI now lives in commonMain (Android/iOS/wasm) — NOT wasm-only
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation("com.arkivanov.decompose:decompose:3.5.0")
                implementation("com.arkivanov.decompose:extensions-compose:3.5.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.3")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity-compose:1.10.1")
            }
        }
    }
}

android {
    namespace = "dev.kmpilot.music"
    compileSdk = 35
    defaultConfig {
        applicationId = "dev.kmpilot.music"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
