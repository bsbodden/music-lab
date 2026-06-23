package dev.kmpilot.music

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.kmpilot.music.presentation.RootComponent
import dev.kmpilot.music.ui.App
import dev.kmpilot.music.ui.buildRoot
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Cadence — a complex, Spotify-style music app built from scratch. Exercises the patterns simple forms don't:
 * a bottom-nav shell, carousels, a collapsing detail header, a persistent mini-player overlay, and a real
 * playback state machine. This wasm entrypoint is the in-browser editor preview; Android/iOS host the same
 * shared App(root) from commonMain.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val scope = CoroutineScope(Dispatchers.Main)
    val root = buildRoot(scope)
    startBridgePosting() // stream the screen graph + live state to the editor (wasm preview only)
    startNavBridge(scope, root) // accept navigate/volume commands from the editor
    ComposeViewport(document.body!!) { App(root) }
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
