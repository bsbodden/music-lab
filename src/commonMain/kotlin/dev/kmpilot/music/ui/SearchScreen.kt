package dev.kmpilot.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import dev.kmpilot.music.domain.Genre
import dev.kmpilot.music.presentation.Phase
import dev.kmpilot.music.presentation.SearchComponent

@Composable
fun SearchScreen(c: SearchComponent) {
    val query by c.query.collectAsState()
    val results by c.results.collectAsState()
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp).testTag("search")) {
        Spacer(Modifier.height(26.dp))
        Text("Search", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query, onValueChange = c::setQuery, singleLine = true,
            placeholder = { Text("Artists, songs, or playlists") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("search_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedContainerColor = Elevated, unfocusedContainerColor = Elevated,
                focusedBorderColor = Accent, unfocusedBorderColor = Color(0xFF3A3A3A), cursorColor = Color.White,
                focusedPlaceholderColor = TextDim, unfocusedPlaceholderColor = TextDim,
                focusedLeadingIconColor = Color.White, unfocusedLeadingIconColor = TextDim,
            ),
        )
        Spacer(Modifier.height(16.dp))
        val phase by c.phase.collectAsState()
        when {
            phase == Phase.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Accent) }
            phase == Phase.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No results for “$query”", color = TextDim) }
            query.isBlank() -> {
                Text("Browse all", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                ) {
                    items(c.genres, key = { it.name }) { g -> GenreTile(g) { c.setQuery(g.name) } }
                }
            }
            else -> LazyColumn { items(results, key = { it.id }) { col -> ResultRow(col) { c.onOpen(col.id) } } }
        }
    }
}

@Composable
private fun GenreTile(g: Genre, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(8.dp)).background(Color(g.accent))
            .clickable(onClick = onClick).padding(12.dp).testTag("genre_${g.name}"),
    ) {
        Text(g.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ResultRow(col: Collection, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp).testTag("result_${col.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverArt(col.coverUrl, col.accent, Modifier.size(52.dp))
        Column(Modifier.padding(start = 12.dp)) {
            Text(col.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(col.subtitle, color = TextDim, style = MaterialTheme.typography.bodySmall)
        }
    }
}
