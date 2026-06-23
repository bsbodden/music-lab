package dev.kmpilot.components.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

/** jvm `actual` for [AsyncImage] — a placeholder (renders [fallback]); the jvm target is a test harness only. */
@Composable
actual fun AsyncImage(url: String, modifier: Modifier, fallback: @Composable () -> Unit) = fallback()

actual fun warmImages(scope: CoroutineScope, urls: List<String>) { /* no-op */ }
