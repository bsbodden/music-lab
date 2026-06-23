package dev.kmpilot.components.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

/**
 * KMPilot component — **AsyncImage**. Loads a remote image and renders it (cover-cropped); shows [fallback]
 * until it's ready, or if it fails. The wasm `actual` fetches the bytes in-browser and decodes them with Skia.
 * The Android/iOS/jvm `actual`s currently just render [fallback] (a placeholder) — real images via Coil3 are a
 * follow-up. Decoded bitmaps are cached by URL on wasm.
 */
@Composable
expect fun AsyncImage(url: String, modifier: Modifier = Modifier, fallback: @Composable () -> Unit = {})

/** Warm the cache for a set of images (call at startup) so screens show real covers immediately. No-op off wasm. */
expect fun warmImages(scope: CoroutineScope, urls: List<String>)
