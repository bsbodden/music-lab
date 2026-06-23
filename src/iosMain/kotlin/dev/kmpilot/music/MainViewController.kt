package dev.kmpilot.music

import androidx.compose.ui.window.ComposeUIViewController
import dev.kmpilot.music.ui.App
import dev.kmpilot.music.ui.buildRoot
import platform.UIKit.UIViewController

/** iOS entrypoint — the iosApp Xcode project hosts this in a SwiftUI UIViewControllerRepresentable. */
fun MainViewController(): UIViewController = ComposeUIViewController { App(buildRoot()) }
