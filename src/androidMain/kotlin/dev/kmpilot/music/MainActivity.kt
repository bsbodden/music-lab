package dev.kmpilot.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.kmpilot.music.ui.App
import dev.kmpilot.music.ui.buildRoot

/** Android entrypoint — hosts the shared App() in a ComponentActivity. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App(buildRoot()) }
    }
}
