package dev.kmpilot.components.media

import android.content.Context

/**
 * Holds the process-wide application [Context] so dep-free KMP components (AudioPlayer, etc.) can reach one.
 * Seeded from [dev.kmpilot.music.MainActivity.onCreate] before any component is built.
 */
object AppContextHolder {
    lateinit var applicationContext: Context
}
