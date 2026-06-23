package dev.kmpilot.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.kmpilot.components.media.AppContextHolder
import dev.kmpilot.music.ui.App
import dev.kmpilot.music.ui.buildRoot

/** Android entrypoint — hosts the shared App() in a ComponentActivity. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Seed the app-wide Context before any component (e.g. ExoPlayer-backed AudioPlayer) is built.
        AppContextHolder.applicationContext = applicationContext
        setContent { App(buildRoot()) }
    }
}
