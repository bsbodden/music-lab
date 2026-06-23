package dev.kmpilot.music

import dev.kmpilot.music.presentation.Phase
import dev.kmpilot.music.presentation.ScreenMachine
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Acceptance criteria for the reusable per-screen content machine: Loading → Content / Empty / Error. */
class ScreenMachineTest {

    @Test fun starts_loading() = runTest {
        val sm = ScreenMachine(backgroundScope, "Home"); sm.start()
        assertEquals(Phase.Loading, sm.phase.value)
    }

    @Test fun loaded_with_items_goes_to_content() = runTest {
        val sm = ScreenMachine(backgroundScope, "Home"); sm.start()
        sm.loaded(empty = false); runCurrent()
        assertEquals(Phase.Content, sm.phase.value)
    }

    @Test fun loaded_with_nothing_goes_to_empty() = runTest {
        val sm = ScreenMachine(backgroundScope, "Search"); sm.start()
        sm.loaded(empty = true); runCurrent()
        assertEquals(Phase.Empty, sm.phase.value)
    }

    @Test fun empty_recovers_to_content_when_results_arrive() = runTest {
        val sm = ScreenMachine(backgroundScope, "Search"); sm.start()
        sm.loaded(empty = true); runCurrent(); assertEquals(Phase.Empty, sm.phase.value)
        sm.loaded(empty = false); runCurrent(); assertEquals(Phase.Content, sm.phase.value)
    }

    @Test fun failure_goes_to_error_then_retry_reloads() = runTest {
        val sm = ScreenMachine(backgroundScope, "Home"); sm.start()
        sm.fail("boom"); runCurrent(); assertEquals(Phase.Error, sm.phase.value)
        sm.retry(); runCurrent(); assertEquals(Phase.Loading, sm.phase.value)
    }
}
