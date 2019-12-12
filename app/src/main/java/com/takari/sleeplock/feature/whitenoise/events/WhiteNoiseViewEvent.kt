package com.takari.sleeplock.feature.whitenoise.events

import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise

/**
 * Represents events WhiteNoiseFragment pushes to WhiteNoiseViewModel.
 */
sealed class WhiteNoiseViewEvent {
    object OnStart : WhiteNoiseViewEvent()
    object OnStop : WhiteNoiseViewEvent()
    object OnStartPauseClick : WhiteNoiseViewEvent()
    object OnOpenWhiteNoiseOptions : WhiteNoiseViewEvent()
    data class OnUserSelectsTime(val millis: Long) : WhiteNoiseViewEvent()
    data class OnUserSelectsWhiteNoise(val whiteNoise: WhiteNoise) : WhiteNoiseViewEvent()
}