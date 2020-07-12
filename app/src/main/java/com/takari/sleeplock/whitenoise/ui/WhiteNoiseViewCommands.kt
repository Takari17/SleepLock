package com.takari.sleeplock.whitenoise.ui

import com.takari.sleeplock.whitenoise.data.WhiteNoise

sealed class WhiteNoiseViewCommands {
    data class StartAndBindToService(val millis: Long, val whiteNoise: WhiteNoise) : WhiteNoiseViewCommands()
    object PauseService : WhiteNoiseViewCommands()
    object ResumeService : WhiteNoiseViewCommands()
    object DestroyService: WhiteNoiseViewCommands()
    object OpenTimeSelectionDialog : WhiteNoiseViewCommands()
    object StartAnimation: WhiteNoiseViewCommands()
}