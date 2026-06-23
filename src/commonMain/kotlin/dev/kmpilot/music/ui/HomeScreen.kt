package dev.kmpilot.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.kmpilot.music.domain.Collection
import dev.kmpilot.music.presentation.HomeComponent
import dev.kmpilot.music.presentation.Phase

@Composable
fun HomeScreen(c: HomeComponent) {
    val phase by c.phase.collectAsState()
    if (phase != Phase.Content) { PhaseGate(phase) {}; return }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp).testTag("home")) {
        item {
            Spacer(Modifier.height(26.dp))
            Text("Good evening", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                c.quickPicks.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { col -> QuickCard(col, Modifier.weight(1f)) { c.onOpen(col.id) } }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(26.dp))
        }
        items(c.shelves, key = { it.first }) { (title, cols) ->
            Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(cols, key = { it.id }) { BigCard(it) { c.onOpen(it.id) } }
            }
            Spacer(Modifier.height(26.dp))
        }
    }
}

@Composable
private fun QuickCard(col: Collection, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier.height(56.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFF2A2A2A))
            .clickable(onClick = onClick).testTag("quick_${col.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverArt(col.coverUrl, col.accent, Modifier.size(56.dp), corner = 6.dp)
        Text(col.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 2,
            overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 10.dp))
    }
}

@Composable
private fun BigCard(col: Collection, onClick: () -> Unit) {
    Column(Modifier.width(150.dp).clickable(onClick = onClick).testTag("card_${col.id}")) {
        CoverArt(col.coverUrl, col.accent, Modifier.size(150.dp), corner = 8.dp)
        Spacer(Modifier.height(8.dp))
        Text(col.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(col.subtitle, color = TextDim, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
