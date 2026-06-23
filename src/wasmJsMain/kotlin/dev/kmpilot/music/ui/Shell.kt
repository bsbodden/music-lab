package dev.kmpilot.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import dev.kmpilot.music.presentation.PlayerState
import dev.kmpilot.music.presentation.RootComponent

/** The persistent now-playing bar that rides above the bottom nav across tabs (the advanced shell pattern). */
@Composable
fun MiniPlayer(state: PlayerState, onOpen: () -> Unit, onToggle: () -> Unit, onStop: () -> Unit) {
    val t = state.track ?: return
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 8.dp).clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2A2A2A)).clickable(onClick = onOpen).padding(8.dp).testTag("mini_player"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverArt(state.coverUrl, state.accent, Modifier.size(42.dp))
        Column(Modifier.weight(1f).padding(start = 10.dp)) {
            Text(t.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(t.artist, color = TextDim, maxLines = 1)
        }
        IconButton(onClick = onToggle, modifier = Modifier.testTag("mini_toggle")) {
            Icon(if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = "Play/Pause", tint = Color.White)
        }
        IconButton(onClick = onStop, modifier = Modifier.testTag("mini_close")) {
            Icon(Icons.Filled.Close, contentDescription = "Stop", tint = TextDim)
        }
    }
}

@Composable
fun BottomBar(active: RootComponent.Config, onSelect: (String) -> Unit) {
    val current = when (active) {
        RootComponent.Config.Search -> "Search"
        RootComponent.Config.Library -> "Library"
        else -> "Home"
    }
    NavigationBar(containerColor = Color(0xFF0A0A0A)) {
        item("Home", Icons.Filled.Home, current, onSelect)
        item("Search", Icons.Filled.Search, current, onSelect)
        item("Library", Icons.Filled.LibraryMusic, current, onSelect)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.item(name: String, icon: ImageVector, current: String, onSelect: (String) -> Unit) {
    NavigationBarItem(
        selected = current == name,
        onClick = { onSelect(name) },
        icon = { Icon(icon, contentDescription = name) },
        label = { Text(name) },
        modifier = Modifier.testTag("tab_$name"),
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White, selectedTextColor = Color.White,
            indicatorColor = Color(0xFF2A2A2A), unselectedIconColor = TextDim, unselectedTextColor = TextDim,
        ),
    )
}
