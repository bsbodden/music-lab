package dev.kmpilot.components.media

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerActionAtItemEndNone
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.actionAtItemEnd
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.setVolume
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * iOS `actual` for [AudioPlayer] — streams a remote https URL with AVFoundation's AVPlayer.
 *
 * AVPlayer is the right primitive for *streaming* a URL (it buffers progressively). All mutating calls
 * (play/pause/seek/volume/item swap) are hopped to the main queue — AVPlayer mutation is only documented as
 * thread-safe on the main thread. Reads (currentTime/duration) are fine off-main. AVPlayer has no native
 * loop flag, so we re-seek to zero on the end-of-item notification when [loop] is set.
 */
@OptIn(ExperimentalForeignApi::class)
actual class AudioPlayer {

    private val player = AVPlayer()

    private var loop: Boolean = false
    private var loopObserver: Any? = null

    private fun onMain(block: () -> Unit) =
        dispatch_async(dispatch_get_main_queue()) { block() }

    actual fun load(src: String, loop: Boolean, volume: Double) {
        this.loop = loop
        val url = NSURL.URLWithString(src) ?: return // bail on malformed URL
        val item = AVPlayerItem(uRL = url) // cinterop name: uRL (capital-R-L)
        onMain {
            player.replaceCurrentItemWithPlayerItem(item)
            player.actionAtItemEnd = AVPlayerActionAtItemEndNone // don't auto-pause at end; we handle it
            player.setVolume(volume.coerceIn(0.0, 1.0).toFloat())
            installLoopObserver(item)
        }
    }

    actual fun play() = onMain { player.play() }
    actual fun pause() = onMain { player.pause() }

    actual fun seek(seconds: Double) = onMain {
        // preferredTimescale 600 is the AVFoundation convention (common multiple of typical frame rates).
        player.seekToTime(CMTimeMakeWithSeconds(seconds, 600))
    }

    actual fun setVolume(v: Double) = onMain {
        player.setVolume(v.coerceIn(0.0, 1.0).toFloat()) // AVPlayer.volume is Float
    }

    // currentTime() returns a CValue<CMTime>; CMTimeGetSeconds takes it directly (passed by value).
    actual fun position(): Double =
        CMTimeGetSeconds(player.currentTime()).orZeroIfNaN()

    actual fun duration(): Double {
        val item = player.currentItem ?: return 0.0
        // While the asset is still loading, duration is kCMTimeIndefinite -> CMTimeGetSeconds == NaN.
        return CMTimeGetSeconds(item.duration).orZeroIfNaN()
    }

    private fun installLoopObserver(item: AVPlayerItem) {
        loopObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        loopObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = item,
            queue = NSOperationQueue.mainQueue,
        ) { _: NSNotification? ->
            if (loop) {
                player.seekToTime(CMTimeMakeWithSeconds(0.0, 600))
                player.play()
            }
        }
    }
}

// CMTimeGetSeconds returns NaN (indefinite/unknown) or +Inf; the transport surface wants a sane Double.
private fun Double.orZeroIfNaN(): Double = if (isNaN() || isInfinite()) 0.0 else this
