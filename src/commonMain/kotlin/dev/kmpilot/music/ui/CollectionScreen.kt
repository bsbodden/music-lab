package dev.kmpilot.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.kmpilot.music.domain.Track
import dev.kmpilot.music.presentation.CollectionComponent
import dev.kmpilot.music.presentation.Phase

@Composable
fun CollectionScreen(c: CollectionComponent) {
    val col = c.collection ?: run { Text("Not found", color = Color.White, modifier = Modifier.padding(24.dp)); return }
    val phase by c.phase.collectAsState()
    if (phase != Phase.Content) { PhaseGate(phase) {}; return }
    val listState = rememberLazyListState()
    // collapsing toolbar: 0 at top (header visible) → 1 once scrolled past the header
    val collapse by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset / 320f).coerceIn(0f, 1f)
        }
    }
    val accent = Color(col.accent)

    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().testTag("collection")) {
            item {
                Column(
                    Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.85f), Bg))).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(64.dp))
                    CoverArt(col.coverUrl, col.accent, Modifier.size(190.dp), corner = 6.dp)
                    Spacer(Modifier.height(18.dp))
                    Text(col.title, color = Color.White, style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(4.dp))
                    Text(col.subtitle, color = TextDim, modifier = Modifier.fillMaxWidth())
                    Text("${c.tracks.size} songs • ${c.minutes} min", color = TextDim,
                        style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        FloatingActionButton(onClick = c::playAll, containerColor = Accent, modifier = Modifier.testTag("play_all")) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.Black)
                        }
                    }
                }
            }
            itemsIndexed(c.tracks, key = { _, t -> t.id }) { idx, t -> TrackRow(idx + 1, t) { c.playFrom(idx) } }
            item { Spacer(Modifier.height(24.dp)) }
        }
        // the collapsing top bar
        Row(
            Modifier.fillMaxWidth().background(accent.copy(alpha = collapse)).padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = c.onBack, modifier = Modifier.testTag("back")) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(col.title, color = Color.White.copy(alpha = collapse), fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun TrackRow(number: Int, track: Track, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp).testTag("track_${track.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("$number", color = TextDim, modifier = Modifier.size(26.dp))
        Column(Modifier.weight(1f)) {
            Text(track.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = TextDim, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
        Text(fmtTime(track.durationSec), color = TextDim, style = MaterialTheme.typography.bodySmall)
    }
}
