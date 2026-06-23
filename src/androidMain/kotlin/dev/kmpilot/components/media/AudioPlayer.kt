package dev.kmpilot.components.media

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

/**
 * Android `actual` for [AudioPlayer] — streams a remote https URL with media3 ExoPlayer.
 *
 * ExoPlayer hides the brittle setDataSource/prepareAsync dance behind setMediaItem/prepare and gives clean
 * position/duration. It needs a [android.content.Context], pulled from the app-wide [AppContextHolder]
 * (seeded in MainActivity.onCreate before any component is built). ExoPlayer must be built/used on the main
 * thread; position/duration are reported in milliseconds (converted to seconds for the transport surface).
 */
actual class AudioPlayer {

    private val player: ExoPlayer = ExoPlayer.Builder(AppContextHolder.applicationContext).build()

    actual fun load(src: String, loop: Boolean, volume: Double) {
        player.setMediaItem(MediaItem.fromUri(src))
        player.repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        player.volume = volume.coerceIn(0.0, 1.0).toFloat()
        player.prepare() // begins buffering the stream; non-blocking
    }

    actual fun play() { player.play() }
    actual fun pause() { player.pause() }
    actual fun seek(seconds: Double) { player.seekTo((seconds * 1000).toLong()) } // ms
    actual fun setVolume(v: Double) { player.volume = v.coerceIn(0.0, 1.0).toFloat() }

    actual fun position(): Double = player.currentPosition / 1000.0 // ms -> s

    actual fun duration(): Double {
        val d = player.duration // ms, or C.TIME_UNSET while loading
        return if (d == C.TIME_UNSET) 0.0 else d / 1000.0
    }

    fun release() { player.release() } // call from the owner's lifecycle teardown
}
