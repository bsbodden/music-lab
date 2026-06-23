package dev.kmpilot.components.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

/** Android `actual` for [AsyncImage] — renders [fallback] (placeholder cover art). Real images via Coil3 are a follow-up. */
@Composable
actual fun AsyncImage(url: String, modifier: Modifier, fallback: @Composable () -> Unit) = fallback()

actual fun warmImages(scope: CoroutineScope, urls: List<String>) { /* no-op */ }
