package com.takari.sleeplock.whitenoise.ui

import com.takari.sleeplock.whitenoise.data.WhiteNoise

/**
 * Represents one time events the ViewModel sends to the view.
 */
sealed class WhiteNoiseOneTimeEvents {
    data class StartAndBindToService(val millis: Long, val whiteNoise: WhiteNoise) : WhiteNoiseOneTimeEvents()
    object PauseService : WhiteNoiseOneTimeEvents()
    object ResumeService : WhiteNoiseOneTimeEvents()
    object DestroyService: WhiteNoiseOneTimeEvents()
}