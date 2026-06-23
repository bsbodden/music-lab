package dev.kmpilot.components.media

/** iOS `actual` for [AudioPlayer] — a no-op placeholder. Real AVPlayer playback is a follow-up. */
actual class AudioPlayer {
    actual fun load(src: String, loop: Boolean, volume: Double) {}
    actual fun play() {}
    actual fun pause() {}
    actual fun seek(seconds: Double) {}
    actual fun setVolume(v: Double) {}
    actual fun position(): Double = 0.0
    actual fun duration(): Double = 0.0
}
