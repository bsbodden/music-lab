package dev.kmpilot.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.kmpilot.components.media.AsyncImage
import dev.kmpilot.music.presentation.Phase

// the app palette (Spotify-ish dark)
val Bg = Color(0xFF121212)
val Elevated = Color(0xFF1C1C1C)
val Accent = Color(0xFF1DB954)
val TextDim = Color(0xFFB3B3B3)

/** Cover art — the real remote image (AsyncImage), with an accent gradient as the fallback while it loads. */
@Composable
fun CoverArt(coverUrl: String?, accent: Long, modifier: Modifier = Modifier, corner: Dp = 6.dp) {
    Box(modifier.clip(RoundedCornerShape(corner))) {
        val gradient: @Composable () -> Unit = {
            val c = Color(accent)
            Box(Modifier.fillMaxSize().background(
                Brush.linearGradient(listOf(c, c.copy(alpha = 0.5f).compositeOver(Color(0xFF0E0E0E)), Color(0xFF161616))),
            ))
        }
        if (coverUrl.isNullOrBlank()) gradient()
        else AsyncImage(coverUrl, Modifier.fillMaxSize(), fallback = gradient)
    }
}

fun fmtTime(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

/** Renders [content] only in the Content phase; otherwise the screen's Loading / Empty / Error state. */
@Composable
fun PhaseGate(phase: Phase, emptyText: String = "Nothing here yet", content: @Composable () -> Unit) {
    when (phase) {
        Phase.Content -> content()
        Phase.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Accent) }
        Phase.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(emptyText, color = TextDim) }
        Phase.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Something went wrong", color = TextDim) }
    }
}
