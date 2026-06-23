package dev.kmpilot.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.kmpilot.music.presentation.RootComponent

@Composable
fun RootContent(root: RootComponent) {
    val childStack by root.stack.subscribeAsState()
    val active = childStack.active.configuration
    val player by root.player.state.collectAsState()
    val onNowPlaying = active is RootComponent.Config.NowPlaying

    Column(Modifier.fillMaxSize().background(Bg)) {
        Box(Modifier.weight(1f)) {
            Children(stack = root.stack) { child ->
                when (val i = child.instance) {
                    is RootComponent.Child.Home -> HomeScreen(i.component)
                    is RootComponent.Child.Search -> SearchScreen(i.component)
                    is RootComponent.Child.Library -> LibraryScreen(i.component)
                    is RootComponent.Child.Collection -> CollectionScreen(i.component)
                    is RootComponent.Child.NowPlaying -> NowPlayingScreen(i.component)
                }
            }
        }
        // the persistent shell: a mini-player above the bottom nav, on every screen except Now Playing
        if (!onNowPlaying) {
            if (player.track != null) MiniPlayer(player, onOpen = root::openNowPlaying, onToggle = { root.player.toggle() }, onStop = { root.player.stop() })
            BottomBar(active, onSelect = { root.selectTab(it) })
        }
    }
}
