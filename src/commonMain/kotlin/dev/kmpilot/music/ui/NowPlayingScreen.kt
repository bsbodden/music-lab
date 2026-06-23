package dev.kmpilot.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.kmpilot.music.presentation.NowPlayingComponent

@Composable
fun NowPlayingScreen(c: NowPlayingComponent) {
    val s by c.player.state.collectAsState()
    val dur = (s.track?.durationSec ?: 1).coerceAtLeast(1)
    Column(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(s.accent).copy(alpha = 0.65f), Bg)))
            .padding(20.dp).testTag("now_playing"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = c.onBack, modifier = Modifier.testTag("np_back")) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Close", tint = Color.White)
            }
            Text("NOW PLAYING", color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(48.dp))
        }
        Spacer(Modifier.weight(1f))
        CoverArt(s.coverUrl, s.accent, Modifier.fillMaxWidth().aspectRatio(1f), corner = 10.dp)
        Spacer(Modifier.height(30.dp))
        Text(s.track?.title ?: "Nothing playing", color = Color.White, style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Text(s.track?.artist ?: "—", color = TextDim, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(18.dp))
        Slider(
            value = s.positionSec.toFloat().coerceIn(0f, dur.toFloat()),
            onValueChange = { c.player.seek(it.toInt()) },
            valueRange = 0f..dur.toFloat(),
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color(0x55FFFFFF)),
            modifier = Modifier.fillMaxWidth().testTag("scrubber"),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(fmtTime(s.positionSec), color = TextDim, style = MaterialTheme.typography.bodySmall)
            Text(fmtTime(dur), color = TextDim, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { c.player.prev() }, modifier = Modifier.testTag("prev")) {
                Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Surface(color = Color.White, shape = CircleShape, modifier = Modifier.size(68.dp).testTag("np_toggle")) {
                IconButton(onClick = { c.player.toggle() }) {
                    Icon(if (s.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = "Play/Pause",
                        tint = Color.Black, modifier = Modifier.size(36.dp))
                }
            }
            IconButton(onClick = { c.player.next() }, modifier = Modifier.testTag("next")) {
                Icon(Icons.Filled.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.weight(1f))
    }
}
