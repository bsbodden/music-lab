package dev.kmpilot.music.data

import dev.kmpilot.music.domain.Collection
import dev.kmpilot.music.domain.Genre
import dev.kmpilot.music.domain.Section
import dev.kmpilot.music.domain.Track

/** The single data seam — home shelves, collection detail, library, genres, and search over the catalogue. */
class MusicRepository {
    private val byTrack = TRACKS.associateBy { it.id }
    private val byCollection = COLLECTIONS.associateBy { it.id }

    fun home(): List<Section> = SECTIONS
    fun quickPicks(): List<Collection> = COLLECTIONS.take(6)
    fun collection(id: String): Collection? = byCollection[id]
    fun tracks(collection: Collection): List<Track> = collection.trackIds.mapNotNull(byTrack::get)
    fun track(id: String): Track? = byTrack[id]
    fun library(): List<Collection> = COLLECTIONS
    fun genres(): List<Genre> = GENRES
    fun coverUrls(): List<String> = (COLLECTIONS.map { it.coverUrl } + TRACKS.map { it.coverUrl }).distinct()

    /** Search collections + tracks; empty query → all collections. */
    fun search(query: String): List<Collection> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return COLLECTIONS
        return COLLECTIONS.filter { c ->
            c.title.lowercase().contains(q) || c.subtitle.lowercase().contains(q) ||
                tracks(c).any { it.title.lowercase().contains(q) || it.artist.lowercase().contains(q) }
        }
    }

    /** The duration of a collection as mm:ss-free total minutes (for the detail header). */
    fun totalMinutes(collection: Collection): Int = (tracks(collection).sumOf { it.durationSec } + 59) / 60
}
