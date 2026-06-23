import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

// Cadence — a complex, Spotify-style music app built from scratch to exercise the UI/nav/state patterns a
// code generator must learn (bottom-nav shell, carousels, collapsing headers, a persistent mini-player
// overlay, a playback state machine). Same opinionated stack (Compose + Decompose + KStateMachine), wasm-only
// with in-memory bundled data. The patterns retarget to any "Spotify for X" vertical.
plugins {
    kotlin("multiplatform") version "2.4.0"
    kotlin("plugin.serialization") version "2.4.0"
    kotlin("plugin.compose") version "2.4.0"
    id("org.jetbrains.compose") version "1.11.1"
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
                implementation("io.github.nsk90:kstatemachine:0.38.1")
                implementation("io.github.nsk90:kstatemachine-coroutines:0.38.1")
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
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation("com.arkivanov.decompose:decompose:3.5.0")
                implementation("com.arkivanov.decompose:extensions-compose:3.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.3")
            }
        }
        val jvmMain by getting {
            dependencies {
                // the Compose compiler plugin is module-wide; the jvm test target needs the runtime on its
                // classpath even though it has no @Composable (the UI is wasm-only).
                implementation(compose.runtime)
            }
        }
    }
}
