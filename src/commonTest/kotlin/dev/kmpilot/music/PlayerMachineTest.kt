package dev.kmpilot.music

import dev.kmpilot.music.domain.Track
import dev.kmpilot.music.presentation.Player
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Acceptance criteria for the playback transport statechart: Idle → Buffering → Playing ⇄ Paused, + Stop. */
class PlayerMachineTest {
    private val t1 = Track("t1", "A", "x", 100, "u1", "c1")
    private val t2 = Track("t2", "B", "y", 120, "u2", "c2")

    @Test fun starts_idle() = runTest {
        val p = Player(backgroundScope); p.start()
        assertEquals("Idle", p.state.value.phase)
        assertFalse(p.state.value.isPlaying)
    }

    @Test fun play_buffers_then_plays() = runTest {
        val p = Player(backgroundScope); p.start()
        p.play(listOf(t1, t2), 0, 0xFF112233)
        advanceTimeBy(300); runCurrent()        // pass the ~250ms buffering delay
        assertEquals("Playing", p.state.value.phase)
        assertTrue(p.state.value.isPlaying)
        assertEquals(t1, p.state.value.track)
        assertEquals(0xFF112233, p.state.value.accent)
    }

    @Test fun pause_and_resume() = runTest {
        val p = Player(backgroundScope); p.start()
        p.play(listOf(t1), 0); advanceTimeBy(300); runCurrent()
        p.toggle(); runCurrent()                 // Playing → Paused (no delay)
        assertEquals("Paused", p.state.value.phase); assertFalse(p.state.value.isPlaying)
        p.toggle(); runCurrent()                 // Paused → Playing
        assertEquals("Playing", p.state.value.phase); assertTrue(p.state.value.isPlaying)
    }

    @Test fun stop_clears_the_queue_so_overlays_dismiss() = runTest {
        val p = Player(backgroundScope); p.start()
        p.play(listOf(t1, t2), 0); advanceTimeBy(300); runCurrent()
        p.stop(); runCurrent()
        assertEquals("Idle", p.state.value.phase)
        assertNull(p.state.value.track)
        assertTrue(p.state.value.queue.isEmpty())
    }

    @Test fun next_advances_to_the_following_track() = runTest {
        val p = Player(backgroundScope); p.start()
        p.play(listOf(t1, t2), 0); advanceTimeBy(300); runCurrent()
        p.next(); advanceTimeBy(300); runCurrent()
        assertEquals(t2, p.state.value.track)
        assertEquals(1, p.state.value.index)
    }

    @Test fun next_wraps_around_the_queue() = runTest {
        val p = Player(backgroundScope); p.start()
        p.play(listOf(t1, t2), 1); advanceTimeBy(300); runCurrent()
        p.next(); advanceTimeBy(300); runCurrent()   // from last → wraps to first
        assertEquals(t1, p.state.value.track)
        assertEquals(0, p.state.value.index)
    }
}
