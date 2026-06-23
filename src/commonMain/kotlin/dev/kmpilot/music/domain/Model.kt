package dev.kmpilot.music.domain

/** Framework-free domain models (POJOs). Audio + cover are real URLs (CC-BY-NC, Internet Archive). */

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val durationSec: Int,
    val audioUrl: String,
    val coverUrl: String,
)

enum class CollectionKind { PLAYLIST, ALBUM }

/** A playlist or album — a real cover image (with an accent-gradient fallback), metadata, and a track list. */
data class Collection(
    val id: String,
    val title: String,
    val subtitle: String,
    val kind: CollectionKind,
    val accent: Long,            // ARGB; the cover gradient fallback
    val coverUrl: String,        // a representative cover
    val trackIds: List<String>,
)

/** A home "shelf" — a titled horizontal carousel of collections. */
data class Section(val title: String, val collectionIds: List<String>)

/** A search category tile. */
data class Genre(val name: String, val accent: Long)
