package dev.kmpilot.music.presentation

import dev.kmpilot.components.media.AudioPlayer
import dev.kmpilot.music.domain.Track
import dev.kmpilot.music.runtime.ChartSpec
import dev.kmpilot.music.runtime.StateSpec
import dev.kmpilot.music.runtime.TransitionSpec
import dev.kmpilot.music.runtime.publishChartSpec
import dev.kmpilot.music.runtime.publishScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.nsk.kstatemachine.event.*
import ru.nsk.kstatemachine.state.*
import ru.nsk.kstatemachine.statemachine.*
import ru.nsk.kstatemachine.transition.*

data class PlayerState(
    val track: Track? = null,
    val queue: List<Track> = emptyList(),
    val index: Int = 0,
    val isPlaying: Boolean = false,
    val positionSec: Int = 0,
    val phase: String = "Idle",
    val accent: Long = 0xFF1DB954,
    val coverUrl: String = "",
)

/**
 * The playback engine — a real transport statechart (Idle → Buffering → Playing ⇄ Paused, + Stop) driving the
 * [AudioPlayer] component, which plays the track's **real** audio (Internet Archive, CC-BY-NC). The scrubber
 * reflects the actual element position; tracks auto-advance on end. `stop()` clears the queue so the overlays
 * (mini-player) dismiss.
 */
class Player(private val scope: CoroutineScope) {
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private lateinit var machine: StateMachine
    private var ticker: Job? = null
    private val audio = AudioPlayer()

    companion object {
        val CHART = ChartSpec(
            id = "NowPlaying", initial = "Idle",
            states = listOf(
                StateSpec("Idle", "empty"), StateSpec("Buffering", "loading"),
                StateSpec("Playing", "content"), StateSpec("Paused", "empty"),
            ),
            transitions = listOf(
                TransitionSpec("Idle", "Buffering", "Play"),
                TransitionSpec("Buffering", "Playing", "Buffered"),
                TransitionSpec("Playing", "Paused", "Pause"),
                TransitionSpec("Paused", "Playing", "Resume"),
                TransitionSpec("Playing", "Buffering", "Skip"),
                TransitionSpec("Paused", "Buffering", "Skip"),
                TransitionSpec("Playing", "Idle", "Stop"),
                TransitionSpec("Paused", "Idle", "Stop"),
            ),
        )
    }

    private object EvPlay : Event
    private object Buffered : Event
    private object EvPause : Event
    private object EvResume : Event
    private object EvSkip : Event
    private object EvStop : Event

    suspend fun start() {
        machine = createStateMachine(scope, name = "NowPlaying") {
            val idle = initialState("Idle")
            val buffering = state("Buffering")
            val playing = state("Playing")
            val paused = state("Paused")
            idle { transition<EvPlay> { targetState = buffering; onTriggered { phase("Buffering") } } }
            buffering {
                transition<Buffered> { targetState = playing; onTriggered { phase("Playing"); startTicker(); audio.play() } }
                transition<EvStop> { targetState = idle; onTriggered { phase("Idle") } }
            }
            playing {
                transition<EvPause> { targetState = paused; onTriggered { phase("Paused"); stopTicker(); audio.pause() } }
                transition<EvSkip> { targetState = buffering; onTriggered { phase("Buffering") } }
                transition<EvStop> { targetState = idle; onTriggered { phase("Idle") } }
            }
            paused {
                transition<EvResume> { targetState = playing; onTriggered { phase("Playing"); startTicker(); audio.play() } }
                transition<EvSkip> { targetState = buffering; onTriggered { phase("Buffering") } }
                transition<EvStop> { targetState = idle; onTriggered { phase("Idle") } }
            }
        }
        publishChartSpec(Json.encodeToString(CHART))
        publishScreenState("NowPlaying", "Idle")
    }

    private fun phase(p: String) {
        _state.value = _state.value.copy(phase = p, isPlaying = p == "Playing")
        publishScreenState("NowPlaying", p)
    }

    fun play(queue: List<Track>, index: Int, accent: Long = _state.value.accent) {
        val t = queue.getOrNull(index)
        _state.value = _state.value.copy(track = t, queue = queue, index = index, positionSec = 0, accent = accent, coverUrl = t?.coverUrl ?: "")
        if (t != null) audio.load(t.audioUrl, loop = false, volume = 0.85)
        scope.launch { machine.processEvent(EvPlay); delay(250); machine.processEvent(Buffered) }
    }

    fun toggle() {
        when (_state.value.phase) {
            "Playing" -> scope.launch { machine.processEvent(EvPause) }
            "Paused" -> scope.launch { machine.processEvent(EvResume) }
            "Idle" -> _state.value.queue.takeIf { it.isNotEmpty() }?.let { play(it, _state.value.index, _state.value.accent) }
            else -> {}
        }
    }

    fun next() = skipTo(_state.value.index + 1)
    fun prev() = skipTo(_state.value.index - 1)

    private fun skipTo(i: Int) {
        val q = _state.value.queue
        if (q.isEmpty()) return
        val idx = ((i % q.size) + q.size) % q.size
        val t = q[idx]
        _state.value = _state.value.copy(track = t, index = idx, positionSec = 0, coverUrl = t.coverUrl)
        audio.load(t.audioUrl, loop = false, volume = 0.85)
        scope.launch { machine.processEvent(EvSkip); delay(250); machine.processEvent(Buffered) }
    }

    fun seek(sec: Int) {
        val dur = _state.value.track?.durationSec ?: return
        val s = sec.coerceIn(0, dur)
        audio.seek(s.toDouble())
        _state.value = _state.value.copy(positionSec = s)
    }

    fun setVolume(v: Double) = audio.setVolume(v)

    /** Stop playback and clear the queue — the persistent overlays (mini-player) hide. */
    fun stop() {
        audio.pause(); stopTicker()
        _state.value = PlayerState()
        scope.launch { machine.processEvent(EvStop) }
    }

    private fun startTicker() {
        stopTicker()
        ticker = scope.launch {
            while (true) {
                delay(500)
                val dur = _state.value.track?.durationSec ?: break
                val pos = audio.position().toInt()
                if (dur > 0 && pos >= dur - 1) { next(); break }   // ended → next track
                _state.value = _state.value.copy(positionSec = pos.coerceIn(0, dur))
            }
        }
    }
    private fun stopTicker() { ticker?.cancel(); ticker = null }
}
