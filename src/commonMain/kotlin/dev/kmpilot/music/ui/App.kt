package dev.kmpilot.music.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import dev.kmpilot.music.data.MusicRepository
import dev.kmpilot.music.presentation.RootComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/** The shared app UI — identical on Android, iOS, and the wasm preview. Each platform entrypoint calls App(root). */
@Composable
fun App(root: RootComponent) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent, background = Bg, surface = Bg,
            onPrimary = Color.Black, onBackground = Color.White, onSurface = Color.White,
        ),
    ) { RootContent(root) }
}

/** Builds the root component (resumed lifecycle + a Main-dispatcher scope). Used by every platform entrypoint. */
fun buildRoot(scope: CoroutineScope = CoroutineScope(Dispatchers.Main)): RootComponent {
    val lifecycle = LifecycleRegistry()
    val root = RootComponent(DefaultComponentContext(lifecycle), scope, MusicRepository())
    lifecycle.resume()
    return root
}
