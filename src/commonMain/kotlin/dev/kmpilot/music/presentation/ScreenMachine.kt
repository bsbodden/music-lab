package dev.kmpilot.music.presentation

import dev.kmpilot.music.runtime.ChartSpec
import dev.kmpilot.music.runtime.StateSpec
import dev.kmpilot.music.runtime.TransitionSpec
import dev.kmpilot.music.runtime.publishChartSpec
import dev.kmpilot.music.runtime.publishScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.nsk.kstatemachine.event.*
import ru.nsk.kstatemachine.state.*
import ru.nsk.kstatemachine.statemachine.*
import ru.nsk.kstatemachine.transition.*

enum class Phase { Loading, Content, Empty, Error }

/**
 * A reusable per-screen content statechart (Loading → Content / Empty / Error) — the canonical async pattern
 * every list/content screen follows. Each screen owns one (Home, Search, Library, Collection); it publishes
 * its own [ChartSpec] (id = the screen name) so the editor's drill-down shows the REAL machine, and its live
 * phase so the active state highlights. Search drives Content↔Empty from its query results.
 */
class ScreenMachine(private val scope: CoroutineScope, private val screen: String) {

    private val _phase = MutableStateFlow(Phase.Loading)
    val phase: StateFlow<Phase> = _phase.asStateFlow()

    private lateinit var machine: StateMachine

    private data class Loaded(val empty: Boolean) : Event
    private data class Failed(val message: String) : Event
    private object Retry : Event

    companion object {
        fun chart(id: String) = ChartSpec(
            id = id, initial = "Loading",
            states = listOf(
                StateSpec("Loading", "loading"), StateSpec("Content", "content"),
                StateSpec("Empty", "empty"), StateSpec("Error", "error"),
            ),
            transitions = listOf(
                TransitionSpec("Loading", "Content", "Loaded[items]"),
                TransitionSpec("Loading", "Empty", "Loaded[none]"),
                TransitionSpec("Loading", "Error", "Failed"),
                TransitionSpec("Content", "Content", "Loaded[items]"),
                TransitionSpec("Content", "Empty", "Loaded[none]"),
                TransitionSpec("Empty", "Content", "Loaded[items]"),
                TransitionSpec("Error", "Loading", "Retry"),
            ),
        )
    }

    suspend fun start() {
        machine = createStateMachine(scope, name = screen) {
            val loading = initialState("Loading")
            val content = state("Content")
            val empty = state("Empty")
            val error = state("Error")
            loading {
                transitionConditionally<Loaded> {
                    direction = { if (event.empty) targetState(empty) else targetState(content) }
                    onTriggered { set(if (it.event.empty) Phase.Empty else Phase.Content) }
                }
                transition<Failed> { targetState = error; onTriggered { set(Phase.Error) } }
            }
            content {
                transitionConditionally<Loaded> {
                    direction = { if (event.empty) targetState(empty) else targetState(content) }
                    onTriggered { set(if (it.event.empty) Phase.Empty else Phase.Content) }
                }
            }
            empty {
                transitionConditionally<Loaded> {
                    direction = { if (event.empty) targetState(empty) else targetState(content) }
                    onTriggered { set(if (it.event.empty) Phase.Empty else Phase.Content) }
                }
            }
            error { transition<Retry> { targetState = loading; onTriggered { set(Phase.Loading) } } }
        }
        publishChartSpec(Json.encodeToString(chart(screen)))
        publishScreenState(screen, "Loading")
    }

    private fun set(p: Phase) { _phase.value = p; publishScreenState(screen, p.name) }

    suspend fun loaded(empty: Boolean) { machine.processEvent(Loaded(empty)) }
    suspend fun fail(message: String) { machine.processEvent(Failed(message)) }
    suspend fun retry() { machine.processEvent(Retry) }
}
