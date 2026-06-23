package dev.kmpilot.music

import dev.kmpilot.music.data.MusicRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Acceptance criteria for the catalogue repository — shelves, quick-picks, search, and the detail header math. */
class MusicRepositoryTest {
    private val repo = MusicRepository()

    @Test fun catalogue_has_the_five_albums() {
        assertEquals(5, repo.library().size)
    }

    @Test fun quick_picks_caps_at_six() {
        assertTrue(repo.quickPicks().size <= 6)
    }

    @Test fun home_has_shelves() {
        assertTrue(repo.home().isNotEmpty())
    }

    @Test fun empty_query_browses_everything() {
        assertEquals(repo.library().size, repo.search("").size)
    }

    @Test fun search_matches_an_album_title() {
        val album = repo.library().first()
        val hits = repo.search(album.title.take(4))
        assertTrue(hits.any { it.id == album.id })
    }

    @Test fun search_with_no_match_is_empty() {
        assertTrue(repo.search("zzxq-not-a-thing").isEmpty())
    }

    @Test fun total_minutes_rounds_up_from_track_seconds() {
        val album = repo.library().first()
        val seconds = repo.tracks(album).sumOf { it.durationSec }
        assertEquals((seconds + 59) / 60, repo.totalMinutes(album))
    }

    @Test fun collection_and_track_lookup_by_id() {
        val album = repo.library().first()
        assertEquals(album, repo.collection(album.id))
        val track = repo.tracks(album).first()
        assertEquals(track, repo.track(track.id))
    }
}
