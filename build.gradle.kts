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
                // Coil3 — real remote images on Android. Uses Coil's BUNDLED OkHttp network layer
                // (coil-network-okhttp) rather than the coil-network-ktor3 path: ktor-client-core 3.2.0 ships
                // methods with spaces in their names, which D8 rejects on this minSdk (needs DEX format 040),
                // breaking dexing of the APK. coil-network-okhttp auto-registers the same network fetcher and
                // pulls no Ktor, so it dexes cleanly and produces identical real-image behavior. (iOS keeps the
                // coil-network-ktor3 + ktor-client-darwin path — Kotlin/Native has no dexing step.)
                implementation("io.coil-kt.coil3:coil-compose:3.2.0")
                implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")
                // media3 ExoPlayer — real audio streaming on Android
                implementation("androidx.media3:media3-exoplayer:1.10.1")
            }
        }
        // iosMain is the intermediate source set created by the default hierarchy template; it does not exist
        // yet when this sourceSets {} block body runs, so configure it via the live `all {}` hook which fires
        // when the template materializes it (eager `by getting` / `named()` are too early here).
        all {
            if (name == "iosMain") {
                dependencies {
                    // Coil3 — real remote images on iOS. coil-network-ktor3 + the Darwin Ktor engine
                    // AUTO-register the network fetcher; no manual ImageLoader setup needed. (Kotlin/Native
                    // has no dexing step, so the ktor path that breaks Android D8 is fine here.)
                    implementation("io.coil-kt.coil3:coil-compose:3.2.0")
                    implementation("io.coil-kt.coil3:coil-network-ktor3:3.2.0")
                    implementation("io.ktor:ktor-client-darwin:3.2.0")
                }
            }
        }
    }
}

// Compose 1.11 + Decompose 3.5 are built against kotlinx-browser 0.3; a newer one changes the DOM-interop
// ABI → ComposeViewport silently fails to attach its canvas (blank preview). Pin it (same as todo-lab).
// NOTE: kept BELOW the kotlin {} block — eagerly realizing all configurations above it would run before the
// default hierarchy template creates the intermediate `iosMain` source set, breaking `iosMain by getting`.
configurations.all {
    resolutionStrategy.force("org.jetbrains.kotlinx:kotlinx-browser:0.3")
}

android {
    namespace = "dev.kmpilot.music"
    compileSdk = 36 // media3 1.10.1 requires compiling against API 36+ (targetSdk stays 35 — runtime behavior unchanged)
    defaultConfig {
        applicationId = "dev.kmpilot.music"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
