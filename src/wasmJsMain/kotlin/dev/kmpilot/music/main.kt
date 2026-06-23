package dev.kmpilot.music

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import dev.kmpilot.music.data.MusicRepository
import dev.kmpilot.music.presentation.RootComponent
import dev.kmpilot.music.ui.Accent
import dev.kmpilot.music.ui.Bg
import dev.kmpilot.music.ui.RootContent
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Cadence — a complex, Spotify-style music app built from scratch. Exercises the patterns simple forms don't:
 * a bottom-nav shell, carousels, a collapsing detail header, a persistent mini-player overlay, and a real
 * playback state machine. Runs live in the editor like the other apps.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()
    val scope = CoroutineScope(Dispatchers.Main)
    val root = RootComponent(DefaultComponentContext(lifecycle), scope, MusicRepository())
    lifecycle.resume()
    startBridgePosting()
    startNavBridge(scope, root)
    ComposeViewport(document.body!!) {
        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = Accent, background = Bg, surface = Bg,
                onPrimary = Color.Black, onBackground = Color.White, onSurface = Color.White,
            ),
        ) { RootContent(root) }
    }
}

private fun startBridgePosting() {
    js("setInterval(function(){ try { if (window.parent && window.parent !== window) { window.parent.postMessage({ type: 'kmpilot', appGraph: globalThis.__appGraph, currentScreen: globalThis.__currentScreen, chartSpec: globalThis.__chartSpec, screen: globalThis.__screen }, '*'); } } catch (e) {} }, 400)")
}

private fun startNavBridge(scope: CoroutineScope, root: RootComponent) {
    installNavListener()
    scope.launch {
        while (true) {
            delay(120)
            val cmd = readPendingNav()
            if (cmd.isNotEmpty()) { clearPendingNav(); root.navigateTo(cmd) }
            val vol = readVolume()
            if (vol >= 0.0) root.player.setVolume(vol)   // the phone frame's volume rocker → device volume
        }
    }
}

private fun installNavListener() {
    js("window.addEventListener('message', function(e){ if (e.data && e.data.type === 'kmpilot-cmd') { if (e.data.navigate) globalThis.__pendingNav = e.data.navigate; if (typeof e.data.volume === 'number') globalThis.__volume = e.data.volume; } })")
}

private fun readPendingNav(): String = js("(globalThis.__pendingNav || '')")
private fun clearPendingNav() { js("globalThis.__pendingNav = null") }
private fun readVolume(): Double = js("(typeof globalThis.__volume === 'number' ? globalThis.__volume : -1)")
