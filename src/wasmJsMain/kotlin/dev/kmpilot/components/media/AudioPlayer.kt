package dev.kmpilot.components.media

/**
 * wasm `actual` for [AudioPlayer] — a thin wrapper over the browser **`HTMLAudioElement`** (the idiomatic
 * Kotlin/Wasm way to play audio, and the same primitive kdroidFilter/ComposeMediaPlayer +
 * Chaintech/ComposeMultiplatformMediaPlayer wrap for wasm). On Android/iOS the same surface is ExoPlayer/AVPlayer.
 */
external interface HtmlAudio : JsAny {
    var src: String
    var currentTime: Double
    val duration: Double
    var loop: Boolean
    var volume: Double
    val paused: Boolean
    fun play()
    fun pause()
}

private fun newAudio(): HtmlAudio = js("new Audio()")

actual class AudioPlayer {
    private val audio: HtmlAudio = newAudio()

    actual fun load(src: String, loop: Boolean, volume: Double) {
        audio.src = src
        audio.loop = loop
        audio.volume = volume
    }

    actual fun play() { audio.play() }            // must follow a user gesture (browser autoplay policy)
    actual fun pause() { audio.pause() }
    actual fun seek(seconds: Double) { audio.currentTime = seconds }
    actual fun setVolume(v: Double) { audio.volume = v.coerceIn(0.0, 1.0) }
    actual fun position(): Double = audio.currentTime
    actual fun duration(): Double = audio.duration
}
