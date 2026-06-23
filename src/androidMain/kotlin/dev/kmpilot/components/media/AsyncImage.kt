package dev.kmpilot.components.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

/**
 * Android `actual` for [AsyncImage] — real remote images via Coil3 (cover-cropped), with [fallback] shown
 * while loading and on error. coil-network-ktor3 + the okhttp Ktor engine auto-register the network fetcher,
 * so no manual ImageLoader setup is needed. [SubcomposeAsyncImage] is used (not AsyncImage) because it is the
 * only one exposing @Composable loading/error slots to host the `fallback: @Composable () -> Unit`.
 */
@Composable
actual fun AsyncImage(url: String, modifier: Modifier, fallback: @Composable () -> Unit) {
    coil3.compose.SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
        loading = { fallback() },
        error = { fallback() },
    )
}

/** No-op: Coil self-caches; warming is unnecessary. */
actual fun warmImages(scope: CoroutineScope, urls: List<String>) { /* no-op: Coil self-caches */ }
