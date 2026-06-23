package dev.kmpilot.components.media

/**
 * KMPilot component — **AudioPlayer**. A thin, platform-neutral transport surface
 * (`load / play / pause / seek / setVolume / position / duration`). The app's statechart owns *transport state*
 * (Playing/Paused/…); this component only owns *the element*. wasm `actual` wraps `HTMLAudioElement`; the jvm
 * test `actual` is a no-op so the [dev.kmpilot.music.presentation.Player] machine is unit-testable.
 */
expect class AudioPlayer() {
    fun load(src: String, loop: Boolean, volume: Double)
    fun play()
    fun pause()
    fun seek(seconds: Double)
    fun setVolume(v: Double)
    fun position(): Double
    fun duration(): Double
}
