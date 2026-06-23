package dev.kmpilot.music.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.kmpilot.music.presentation.LibraryComponent
import dev.kmpilot.music.presentation.Phase

@Composable
fun LibraryScreen(c: LibraryComponent) {
    val phase by c.phase.collectAsState()
    if (phase != Phase.Content) { PhaseGate(phase) {}; return }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp).testTag("library")) {
        Spacer(Modifier.height(26.dp))
        Text("Your Library", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(c.items, key = { it.id }) { col ->
                Row(
                    Modifier.fillMaxWidth().clickable { c.onOpen(col.id) }.padding(vertical = 8.dp).testTag("lib_${col.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CoverArt(col.coverUrl, col.accent, Modifier.size(56.dp))
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(col.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(col.subtitle, color = TextDim, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
